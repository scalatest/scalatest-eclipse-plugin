/*
 * SCALA LICENSE
 *
 * Copyright (C) 2011-2012 Artima, Inc. All rights reserved.
 *
 * This software was developed by Artima, Inc.
 *
 * Permission to use, copy, modify, and distribute this software in source
 * or binary form for any purpose with or without fee is hereby granted,
 * provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the EPFL nor the names of its contributors
 *    may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package scala.tools.eclipse.scalatest.launching

import scala.tools.eclipse.ScalaPresentationCompiler
import org.scalatest.finders.AstNode
import scala.annotation.tailrec
import org.eclipse.jdt.core.IJavaElement
import scala.tools.eclipse.javaelements.ScalaClassElement
import scala.tools.eclipse.javaelements.ScalaElement
import scala.tools.eclipse.ScalaPlugin
import org.eclipse.core.resources.IProject
import scala.tools.eclipse.javaelements.ScalaCompilationUnit
import scala.tools.nsc.util.OffsetPosition
import org.eclipse.jface.text.ITextSelection
import org.scalatest.finders.Selection
import scala.tools.nsc.util.BatchSourceFile

class ScalaTestFinder(val compiler: ScalaPresentationCompiler, loader: ClassLoader) {
  
  import compiler._

  trait TreeSupport {
  
    def getParent(className: String, root: Tree, node: Tree): AstNode = {
      val parentTreeOpt = getParentTree(root, node)
      parentTreeOpt match {
        case Some(parentTree) => {
          val skippedParentTree = skipApplyToImplicit(parentTree, root)
          transformAst(className, skippedParentTree, root).getOrElse(null)
        }
        case None => 
          null
      }
    }
    
    @tailrec
    private def skipApplyToImplicit(nodeTree: Tree, rootTree: Tree): Tree = {
      nodeTree match {
        case _: ApplyToImplicitArgs | 
             _: ApplyImplicitView =>  
          val nextParentOpt = getParentTree(rootTree, nodeTree)
          nextParentOpt match {
            case Some(nextParent) => 
              skipApplyToImplicit(nextParent, rootTree)
            case None => 
              nodeTree
          }
        case _ =>
          nodeTree
      }
    }
    
    @tailrec
    private def findBlock(apply: Apply): Option[Block] = {
      apply.args.lastOption match {
        case Some(b: Block) => 
          Some(b)
        case _ =>
          apply.args match {
            case List(a: Apply, _*) =>
              findBlock(a)
            case _ =>
              None
          }
      }
    }
  
    def getChildren(className: String, root: Tree, node: Tree): Array[AstNode] = {
      val children = node match {
        case implArgs: ApplyToImplicitArgs => 
          implArgs.children.head match {
            case implApply: Apply => 
              implApply.children.last.children
            case _ =>
              node.children
          }
        case apply: Apply =>
          val blockOpt = findBlock(apply)
          blockOpt match {
            case Some(block) => 
              block.children
            case None => 
              List.empty
          }
        case _ =>
          node.children
      }
      children.map(mapAst(className, _, root)).filter(_.isDefined).map(_.get).toArray
    }
  }
  
  private case class ConstructorBlock(pClassName: String, rootTree: Tree, nodeTree: Tree) 
    extends org.scalatest.finders.ConstructorBlock(pClassName, Array.empty) with TreeSupport {
    override lazy val children = {
      val rawChildren = getChildren(pClassName, rootTree, nodeTree).toList
      // Remove the primary constructor method definition.
      // the following does not work for some reason, may be because of varargs?
      /*rawChildren match {
        case MethodDefinition(_, _, _, "this", _) :: rest  =>
          org.eclipse.jface.dialogs.MessageDialog.openInformation(null, "With Primary Constructor", rest.map(getNodeDisplay(_)).mkString("\n===============\n"))
          rest.toArray
        case _ =>
          org.eclipse.jface.dialogs.MessageDialog.openInformation(null, "Without Primary Constructor", rawChildren.map(getNodeDisplay(_)).mkString("\n===============\n"))
          rawChildren.toArray
      }*/
      
      if (rawChildren.size > 0 && rawChildren.head.isInstanceOf[MethodDefinition] && rawChildren.head.asInstanceOf[MethodDefinition].pName == "this") 
        rawChildren.tail.toArray
      else 
        rawChildren.toArray
    }
    override def equals(other: Any) = if (other != null && other.isInstanceOf[ConstructorBlock]) nodeTree eq other.asInstanceOf[ConstructorBlock].nodeTree else false 
    override def hashCode = nodeTree.hashCode
  }

  private case class MethodDefinition(
    pClassName: String,
    rootTree: Tree,
    nodeTree: Tree,
    pName: String, 
    pParamTypes: String*)
    extends org.scalatest.finders.MethodDefinition(pClassName, null, Array.empty, pName, pParamTypes.toList: _*) with TreeSupport {
    override def parent() = getParent(pClassName, rootTree, nodeTree)
    override lazy val children = getChildren(pClassName, rootTree, nodeTree)
    override def equals(other: Any) = if (other != null && other.isInstanceOf[MethodDefinition]) nodeTree eq other.asInstanceOf[MethodDefinition].nodeTree else false
    override def hashCode = nodeTree.hashCode
  }
  
  private case class MethodInvocation(
    pClassName: String,
    pTarget: AstNode, 
    rootTree: Tree,
    nodeTree: Tree,
    pName: String, 
    pArgs: AstNode*)
    extends org.scalatest.finders.MethodInvocation(pClassName, pTarget, null, Array.empty, pName, pArgs.toList: _*) with TreeSupport {
    override def parent() = getParent(pClassName, rootTree, nodeTree)
    override lazy val children = getChildren(pClassName, rootTree, nodeTree)
    override def equals(other: Any) = if (other != null && other.isInstanceOf[MethodInvocation]) nodeTree eq other.asInstanceOf[MethodInvocation].nodeTree else false
    override def hashCode = nodeTree.hashCode
  }
  
  private case class StringLiteral(pClassName: String, rootTree: Tree, nodeTree: Tree, pValue: String)
    extends org.scalatest.finders.StringLiteral(pClassName, null, pValue) with TreeSupport {
    override def parent() = getParent(pClassName, rootTree, nodeTree)
    override def equals(other: Any) = if (other != null && other.isInstanceOf[StringLiteral]) nodeTree eq other.asInstanceOf[StringLiteral].nodeTree else false
    override def hashCode = nodeTree.hashCode
  }
  
  private case class ToStringTarget(pClassName: String, rootTree: Tree, nodeTree: Tree, pTarget: AnyRef) 
    extends org.scalatest.finders.ToStringTarget(pClassName, null, Array.empty, pTarget) with TreeSupport {
    override def parent() = getParent(pClassName, rootTree, nodeTree)
    override lazy val children = getChildren(pClassName, rootTree, nodeTree)
    override def equals(other: Any) = if (other != null && other.isInstanceOf[ToStringTarget]) nodeTree eq other.asInstanceOf[ToStringTarget].nodeTree else false
    override def hashCode = nodeTree.hashCode
  }
  
  @tailrec
  final def getParentTree(candidate: Tree, node: Tree): Option[Tree] = {
    val foundOpt = candidate.children.find(c => c == node)
    foundOpt match {
      case Some(a) =>
        Some(candidate)
      case _ =>
        val nextCandidateOpt = candidate.children.find(c => c.pos includes node.pos)
        nextCandidateOpt match {
          case Some(nextCandidate) => 
            getParentTree(nextCandidate, node)
          case None => 
            None
        }
    }
  }
   
  private def getTarget(className: String, apply: GenericApply, rootTree: Tree): AstNode = {
    apply.fun match {
      case Select(Literal(value), _) => 
        new ToStringTarget(className, rootTree, apply, value.stringValue)
      case Select(impl: ApplyImplicitView, _) =>
        val implFirstArg: Tree = impl.args(0)
        implFirstArg match {
          case Literal(value) =>
            new ToStringTarget(className, rootTree, apply, value.stringValue)
          case Apply(fun: Apply, _) => 
            mapApplyToMethodInvocation(className, fun, rootTree)
          case Apply(fun, _) => 
            new ToStringTarget(className, rootTree, impl, fun)
          case _ => 
            new ToStringTarget(className, rootTree, impl, implFirstArg.toString)
        }
      case Select(apply: Apply, _) => 
        mapApplyToMethodInvocation(className, apply, rootTree)
      case Select(select: Select, _) => 
        new ToStringTarget(className, rootTree, select, select.name)
      case select: Select => 
        new ToStringTarget(className, rootTree, select.qualifier, select.name)
      case funApply: Apply => 
        getTarget(className, funApply, rootTree)
      case typeApply: TypeApply => 
        getTarget(className, typeApply, rootTree)
      case other =>
        new ToStringTarget(className, rootTree, apply.fun, apply.fun.toString)
    }
  }
  
  private def mapApplyToMethodInvocation(className: String, apply: Apply, rootTree: Tree): MethodInvocation = {
    val target = getTarget(className, apply, rootTree)
    val name = apply.symbol.decodedName
    val rawArgs = if (apply.fun.hasSymbol) apply.args else apply.fun.asInstanceOf[GenericApply].args
    val args = rawArgs.map(arg => arg match {
      case lit: Literal =>
        new StringLiteral(className, rootTree, apply, lit.value.stringValue)
      case _ =>
        new ToStringTarget(className, rootTree, apply, arg.toString)
    })
    new MethodInvocation(className, target, rootTree, apply, name, args: _*)
  }
  
  private def mapAst(className: String, selectedTree: Tree, rootTree: Tree): Option[AstNode] = {    
    selectedTree match {
      case defDef: DefDef =>
        val defDefSym = defDef.symbol
        // Some approaches used before
        // param types: " + defDefSym.info.paramTypes.map(t => t.typeSymbol.fullName)
        // param types: " + defDef.vparamss.flatten.toList.map(valDef => valDef.tpt.symbol.fullName)
        val args = compiler.askOption[List[String]](() => defDefSym.info.paramTypes.map(t => t.typeSymbol.fullName)).getOrElse(List.empty)
        Some(new MethodDefinition(className, rootTree, selectedTree, defDefSym.decodedName, args: _*))
      case applyImplicitView: ApplyImplicitView =>
        None
      case apply: Apply =>
        Some(mapApplyToMethodInvocation(className, apply, rootTree))
      case template: Template =>
        Some(new ConstructorBlock(className, rootTree, selectedTree))
      case _ =>
        None
    }
  }
  
  @tailrec
  private def transformAst(className: String, selectedTree: Tree, rootTree: Tree): Option[AstNode] = {
    val astNodeOpt = mapAst(className, selectedTree, rootTree)
    astNodeOpt match {
      case Some(astNode) => astNodeOpt
      case None => 
        val parentOpt = getParentTree(rootTree, selectedTree)
        parentOpt match {
          case Some(parent) =>
            transformAst(className, parent, rootTree)
          case None =>
            None
        }
    }
  }
  
  // this should be removed since @Style is already removed from 2.0, it only works for version prior to 2.0.M4.
  private def getFinderByStyleAnnotation(annotations: List[AnnotationInfo]): Option[String] = 
    annotations.find(aInfo => aInfo.atp.toString == "org.scalatest.Style") match {
      case Some(styleAnnotation) => 
        styleAnnotation.assocs.find(a => a._1.toString == "value") match {
          case Some(annotationValue) => 
            annotationValue._2 match {
              case LiteralAnnotArg(const) => Some(const.stringValue)
              case _ => None
            }
          case None => None
        }
      case None => None
    }
  
  private def getFinderByFindersAnnotation(annotations: List[AnnotationInfo]): Array[String] = 
    annotations.find(aInfo => aInfo.atp.toString == "org.scalatest.Finders") match {
      case Some(findersAnnotation) => 
        findersAnnotation.assocs.find(a => a._1.toString == "value") match {
          case Some(annotationValue) =>
            annotationValue._2 match {
              case ArrayAnnotArg(args) => 
                args.filter(_.isInstanceOf[LiteralAnnotArg]).map(_.asInstanceOf[LiteralAnnotArg].const.stringValue)
              case _ => Array.empty
            }
          case None => Array.empty
        }
      case None => Array.empty
    }
  
  private def getFinderClassNames(annotations: List[AnnotationInfo]): Array[String] = {
    val finderClassNames = getFinderByFindersAnnotation(annotations)
    if (finderClassNames.size > 0) 
      finderClassNames
    else {
      getFinderByStyleAnnotation(annotations) match {
        case Some(finder) => Array(finder)
        case None => Array.empty
      }
    }
  }
  
  def find(textSelection: ITextSelection, element: IJavaElement): Option[Selection] = {
    element match {
      case scElement: ScalaElement => 
        val classElement = ScalaTestLaunchShortcut.getClassElement(element)
        if (classElement != null) {
          // Let's get the ClassDef for the classElement
          val scu = scElement.getCompilationUnit.asInstanceOf[ScalaCompilationUnit]
          val response = new Response[Tree]
          compiler.askParsedEntered(new BatchSourceFile(scu.file, scu.getContents), false, response)
          response.get match {
            case Left(tree) => tree.children.find {
              case classDef: ClassDef if classDef.symbol.fullName == classElement.getFullyQualifiedName => true
              case _ => false
            } match {
              case Some(classDef: ClassDef) => 
                // We got ClassDef
                val wrapWithAnnotation = classDef.symbol.annotations.find(a => a.atp.toString == "org.scalatest.WrapWith")
                
                val finderClassNames: Array[String] = 
                  wrapWithAnnotation match {
                    case Some(wrapWithAnnotation) =>
                      // @WrapWith found, will lookup the @Style from the runner.
                      wrapWithAnnotation.assocs.find(a => a._1.toString == "value") match {
                        case Some(tuple) => 
                          tuple._2 match {
                            case LiteralAnnotArg(const) => 
                              val runnerSymbol = const.typeValue
                              getFinderClassNames(runnerSymbol.typeSymbol.annotations)
                            case _ => Array.empty
                          }
                        case None => Array.empty
                      }
                    case None =>
                      // No @WrapWith found, will lookup the @Style from super classes in linearized order
                      val linearizedBaseClasses = compiler.askOption[List[Symbol]](() => classDef.symbol.info.baseClasses).getOrElse(List.empty)
                      getFinderClassNames(linearizedBaseClasses.flatMap(_.annotations))
                  }
                
                var selectionOpt: Option[Selection] = None
                finderClassNames.find { finderClassName =>
                  val finderClass = loader.loadClass(finderClassName)
                  val finder = finderClass.newInstance
                  val position = new OffsetPosition(new BatchSourceFile(scu.file, scu.getContents), textSelection.getOffset)
                  //val selectedTree = compiler.locateTree(position)
                  val response = new Response[Tree]
                  compiler.askTypeAt(position, response)
                  val selectedTree = response.get match {
                    case Left(tree) => tree 
                    case Right(thr) => throw thr
                  }

                  val scalatestAstOpt = transformAst(classElement.getFullyQualifiedName, selectedTree, classDef)
                  scalatestAstOpt match {
                    case Some(scalatestAst) => 
                      val parent = scalatestAst.parent
                      val findMethod = finder.getClass.getMethods.find { mtd =>
                        mtd.getName == "find" && mtd.getParameterTypes.length == 1 && mtd.getParameterTypes()(0).getName == "org.scalatest.finders.AstNode"
                      }.get
                      val selection = findMethod.invoke(finder, scalatestAst)
                      if (selection != null) {
                        selectionOpt = Some(selection.asInstanceOf[Selection])
                        true
                      }
                      else
                        false
                    case None => false
                  }
                }
                selectionOpt
                
              case _ =>
                None
            }
            case Right(thr) => 
              None
          }
        }
        else
          None
      case _ => 
        None
    }
  }
}
