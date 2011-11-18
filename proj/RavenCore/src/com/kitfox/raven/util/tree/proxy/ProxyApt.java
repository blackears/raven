/*
 * Copyright 2011 Mark McKay
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kitfox.raven.util.tree.proxy;

import com.kitfox.raven.util.tree.NodeObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 *
 * @author kitfox
 */
public class ProxyApt extends AbstractProcessor
{

    public ProxyApt()
    {
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.RELEASE_6;
    }


    @Override
    public Set<String> getSupportedAnnotationTypes()
    {

        return Collections.singleton(ProxyMarkObject.class.getName());
    }

    @Override
    public Set<String> getSupportedOptions()
    {
        HashSet<String> set = new HashSet<String>();
        return set;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        for (Element ele : roundEnv.getElementsAnnotatedWith(ProxyMarkObject.class))
        {
            if (ele.getKind() != ElementKind.CLASS)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "Only classes should have @ProxyMarkObject: ", ele);
                continue;
            }


            TypeElement classEle = (TypeElement)ele;

            Element pkgEle = classEle.getEnclosingElement();
            while (pkgEle.getKind() != ElementKind.PACKAGE)
            {
                pkgEle = pkgEle.getEnclosingElement();
            }

            String providerQname = buildClassName(classEle);
            String classSimpleName = classEle.getSimpleName().toString();

//            for (Class cls: classEle.getSuperclass().getClass().getInterfaces())
//            {
//                System.err.println("***" + cls);
//            }

            DeclaredType superType = (DeclaredType)classEle.getSuperclass();
            String superClassName = ((TypeElement)superType.asElement()).toString();
            if (NodeObject.class.getCanonicalName().equals(superClassName))
            {
                superClassName = "ProxyObject";
            }
            else
            {
//                superClassName = "ProxyObject";
                superClassName = superClassName + "Proxy";
            }

            Filer filer = processingEnv.getFiler();
            try
            {
                JavaFileObject f = filer.createSourceFile(providerQname + "Proxy");

                Writer out = f.openWriter();
                PrintWriter pw = new PrintWriter(out);

                pw.printf("package %s;\n", pkgEle.toString());
                pw.println();
                pw.println("import com.kitfox.raven.util.service.ServiceAnno;");
                pw.println("import com.kitfox.raven.util.tree.proxy.ProxyObject;");
                pw.println("import com.kitfox.raven.util.tree.proxy.ProxyProvider;");
                pw.println("import com.kitfox.raven.util.tree.ChildWrapperList;");
                pw.println("import com.kitfox.raven.util.tree.ChildWrapperList.AddChildAction;");
                pw.println("import com.kitfox.raven.util.tree.PropertyDataInline;");
                pw.println("import com.kitfox.raven.util.undo.HistoryAction;");

                pw.println();
                pw.printf("public class %sProxy<T extends %s> extends %s<T>\n", classSimpleName, classSimpleName, superClassName);
                pw.println("{");
                pw.printf("    public %sProxy(T node)\n", classSimpleName);
                pw.println("    {");
                pw.printf("        super(node);\n");
                pw.println("    }");
                pw.println();

    //            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
    //                    "Processing " + classEle, classEle);

                for (Element childEle: classEle.getEnclosedElements())
                {
                    StringBuilder iface = new StringBuilder();
                    for (Class cls: childEle.getClass().getInterfaces())
                    {
                        iface.append(cls.getCanonicalName()).append(", ");
                    }

    //                String msg = "Child"
    //                        + "\n" + childEle
    //                        + "\n" + childEle.getKind()
    //                        + "\n" + iface
    //                        + "\n" + childEle.asType();
    //                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
    //                        msg, childEle);

                    if (childEle.getKind() == ElementKind.FIELD)
                    {
    //                    TypeMirror mirror = childEle.asType();
    //                    mirror.
                        processField((VariableElement)childEle, providerQname, pw);
                    }
                    else if (childEle.getKind() == ElementKind.METHOD)
                    {
                        processMethod((ExecutableElement)childEle, providerQname, pw);
                    }
                }


                pw.println();
                pw.println("    @ServiceAnno(service=ProxyProvider.class)");
                pw.println("    public static class Provider extends ProxyProvider");
                pw.println("    {");
                pw.println("        public Provider()");
                pw.println("        {");
                pw.printf("            super(%sProxy.class, %s.class);\n", classSimpleName, classSimpleName);
                pw.println("        }");
                pw.println("    }");

//    @ServiceAnno(service=ProxyProvider.class)
//    public static class Provider extends ProxyProvider
//    {
//        public Provider()
//        {
//            super(BarProxy.class, Bar.class);
//        }
//    }

                pw.println("}");
                pw.println();

                pw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }


//            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
//                    writer.toString(), ele);

//            System.err.println("*******");
//            System.err.println(writer.toString());
        }

        return true;
    }

    private void processField(VariableElement ele, String nodeType, PrintWriter pw)
    {
//        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
//                "Processing " + ele, ele);

        {
            ProxyMarkProperty prop = ele.getAnnotation(ProxyMarkProperty.class);
            if (prop != null)
            {
                processProperty(ele, prop, pw);
                return;
            }
        }

        {
            ProxyMarkChildSingle prop = ele.getAnnotation(ProxyMarkChildSingle.class);
            if (prop != null)
            {
                processChildSingle(ele, prop, pw);
                return;
            }
        }

        {
            ProxyMarkChildList prop = ele.getAnnotation(ProxyMarkChildList.class);
            if (prop != null)
            {
                processChildList(ele, prop, nodeType, pw);
                return;
            }
        }
    }

    private void processChildList(VariableElement ele, ProxyMarkChildList prop, String nodeType, PrintWriter pw)
    {
        Set<Modifier> mods = ele.getModifiers();

        if (!mods.contains(Modifier.FINAL))
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Child wrappers must be declared final", ele);
            return;
        }

        if (mods.contains(Modifier.PRIVATE))
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Child wrappers cannot be declared private", ele);
            return;
        }

        String typeName = null;
        try
        {
            Class cls = prop.type();
            typeName = cls.getCanonicalName();
        }
        catch (MirroredTypeException ex)
        {
            typeName = buildClassName((TypeElement)((DeclaredType)ex.getTypeMirror()).asElement());
        }

        String propName = prop.name();
        Name fieldName = ele.getSimpleName();

        pw.printf("    //--------------------------\n");
        pw.printf("    // Child list %s\n", fieldName.toString());
        pw.printf("\n");

        pw.printf("    public int getNum%s()\n", capFirst(propName));
        pw.printf("    {\n");
        pw.printf("        return node.%s.getNumChildren();\n", fieldName.toString());
        pw.printf("    }\n");
        pw.printf("\n");

        pw.printf("    public %sProxy get%s(int index)\n", typeName, capFirst(propName));
        pw.printf("    {\n");
        pw.printf("        return new %sProxy(node.%s.getChild(index));\n", typeName, fieldName.toString());
        pw.printf("    }\n");
        pw.printf("\n");

        pw.printf("    public int indexOf%s(%sProxy child)\n", capFirst(propName), typeName);
        pw.printf("    {\n");
        pw.printf("        return node.%s.indexOfChild((%s)getNode(child));\n", fieldName.toString(), typeName);
        pw.printf("    }\n");
        pw.printf("\n");

        pw.printf("    public void add%s(%sProxy child)\n", capFirst(propName), typeName);
        pw.printf("    {\n");
        pw.printf("        add%s(getNum%s(), child);\n", capFirst(propName), capFirst(propName));
        pw.printf("    }\n");
        pw.printf("\n");

        pw.printf("    public void add%s(int index, %sProxy child)\n", capFirst(propName), typeName);
        pw.printf("    {\n");
        pw.printf("        AddChildAction action = node.%s.addChild(index, (%s)getNode(child));\n", fieldName.toString(), typeName);
        pw.printf("        node.getDocument().getHistory().add(action);\n");
        pw.printf("    }\n");
        pw.printf("\n");

        pw.printf("    public void remove%s(%sProxy child)\n", capFirst(propName), typeName);
        pw.printf("    {\n");
        pw.printf("        remove%s(indexOf%s(child));\n", capFirst(propName), capFirst(propName));
        pw.printf("    }\n");
        pw.printf("\n");

        pw.printf("    public %sProxy remove%s(int index)\n", typeName, capFirst(propName));
        pw.printf("    {\n");
        pw.printf("        ChildWrapperList<%s, %s>.RemoveChildAction action =\n", nodeType, typeName);
        pw.printf("                node.%s.removeChild(index);\n", fieldName.toString());
        pw.printf("        node.getDocument().getHistory().add(action);\n");
        pw.printf("        return new %sProxy(action.getChild());\n", typeName);
        pw.printf("    }\n");
        pw.printf("\n");

    }

    private void processChildSingle(VariableElement ele, ProxyMarkChildSingle prop, PrintWriter pw)
    {
        Set<Modifier> mods = ele.getModifiers();

        if (!mods.contains(Modifier.FINAL))
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Child wrappers must be declared final", ele);
            return;
        }

        if (mods.contains(Modifier.PRIVATE))
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Child wrappers cannot be declared private", ele);
            return;
        }

        String typeName = null;
        try
        {
            Class cls = prop.type();
            typeName = cls.getCanonicalName();
        }
        catch (MirroredTypeException ex)
        {
            typeName = buildClassName((TypeElement)((DeclaredType)ex.getTypeMirror()).asElement());
        }

        String propName = prop.name();
        Name fieldName = ele.getSimpleName();


        pw.printf("    //--------------------------\n");
        pw.printf("    // Child single %s\n", fieldName.toString());
        pw.printf("\n");

        pw.printf("    public %sProxy get%s()\n", typeName, capFirst(propName));
        pw.printf("    {\n");
        pw.printf("        return new %sProxy(node.%s.getChild(0));\n", typeName, fieldName.toString());
        pw.printf("    }\n");
        pw.printf("\n");

    }

    private void processProperty(VariableElement ele, ProxyMarkProperty prop, PrintWriter pw)
    {
        Set<Modifier> mods = ele.getModifiers();

        if (!mods.contains(Modifier.FINAL))
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Properties must be declared final", ele);
            return;
        }

        if (mods.contains(Modifier.PRIVATE))
        {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "Properties cannot be declared private", ele);
            return;
        }

        String typeName = null;
        try
        {
            Class cls = prop.type();
            typeName = cls.getCanonicalName();
        }
        catch (MirroredTypeException ex)
        {
            typeName = buildClassName((TypeElement)((DeclaredType)ex.getTypeMirror()).asElement());
        }

        String propName = prop.name();
        Name fieldName = ele.getSimpleName();

        pw.printf("    //--------------------------\n");
        pw.printf("    // Property %s\n", fieldName.toString());
        pw.printf("\n");

        pw.printf("    public %s get%s()\n", typeName, capFirst(propName));
        pw.printf("    {\n");
        pw.printf("        return node.%s.getValue().getValue();\n", fieldName.toString());
        pw.printf("    }\n");
        pw.printf("\n");

        pw.printf("    public void set%s(%s value)\n", capFirst(propName), typeName);
        pw.printf("    {\n");
        pw.printf("        HistoryAction action = node.%s.setValue(new PropertyDataInline<%s>(value));\n", fieldName.toString(), typeName);
        pw.printf("        node.getDocument().getHistory().add(action);\n");
        pw.printf("    }\n");
        pw.printf("\n");

    }


    private void processMethod(ExecutableElement ele, String nodeType, PrintWriter pw)
    {

        {
            ProxyMarkHistoryAction prop = ele.getAnnotation(ProxyMarkHistoryAction.class);
            if (prop != null)
            {
                processHistoryAction(ele, prop, pw);
                return;
            }
        }

        {
            ProxyMarkMethod prop = ele.getAnnotation(ProxyMarkMethod.class);
            if (prop != null)
            {
                processMethod(ele, prop, pw);
                return;
            }
        }
    }

    private String[] parseParameters(ExecutableElement ele)
    {
        StringBuilder paramImplSb = new StringBuilder();
        StringBuilder paramDeclSb = new StringBuilder();

        List<? extends VariableElement> paramList = ele.getParameters();
        for (int i = 0; i < paramList.size(); ++i)
        {
            VariableElement paramEle = paramList.get(i);
            TypeMirror type = paramEle.asType();
            String paramName = paramEle.getSimpleName().toString();
            ProxyMarkParameter anno = paramEle.getAnnotation(ProxyMarkParameter.class);

            if (anno == null)
            {
                paramDeclSb.append(type.toString()).append(' ')
                        .append(paramName);
                paramImplSb.append(paramName);
            }
            else
            {
                paramDeclSb.append(type.toString()).append("Proxy ")
                        .append(paramName);
                paramImplSb.append('(').append(type.toString())
                        .append(")getNode(").append(paramName).append(')');
            }

            if (i != paramList.size() - 1)
            {
                paramDeclSb.append(", ");
                paramImplSb.append(", ");
            }
        }

        return new String[]{paramDeclSb.toString(), paramImplSb.toString()};
    }

    private void processHistoryAction(ExecutableElement ele, ProxyMarkHistoryAction prop, PrintWriter pw)
    {
        String[] params = parseParameters(ele);

        String paramDecl = params[0];
        String paramImpl = params[1];

        String methName = ele.getSimpleName().toString();


        pw.printf("    //--------------------------\n");
        pw.printf("    // History action %s\n", methName);
        pw.printf("\n");

        pw.printf("    public void %s(%s)\n", methName, paramDecl);
        pw.printf("    {\n");
        pw.printf("        HistoryAction action = node.%s(%s);\n", methName, paramImpl);
        pw.printf("        node.getDocument().getHistory().add(action);\n");
        pw.printf("    }\n");
        pw.printf("\n");

    }

    private void processMethod(ExecutableElement ele, ProxyMarkMethod prop, PrintWriter pw)
    {
        String[] params = parseParameters(ele);

        String paramDecl = params[0];
        String paramImpl = params[1];

        String methName = ele.getSimpleName().toString();
        String retType = ele.getReturnType().toString();


        pw.printf("    //--------------------------\n");
        pw.printf("    // Method %s\n", methName);
        pw.printf("\n");

        if ("void".equals(retType))
        {
            pw.printf("    public void %s(%s)\n", methName, paramDecl);
            pw.printf("    {\n");
            pw.printf("        node.%s(%s);\n", methName, paramImpl);
            pw.printf("    }\n");
            pw.printf("\n");
        }
        else if (!prop.proxy())
        {
            pw.printf("    public %s %s(%s)\n", retType, methName, paramDecl);
            pw.printf("    {\n");
            pw.printf("        return node.%s(%s);\n", methName, paramImpl);
            pw.printf("    }\n");
            pw.printf("\n");
        }
        else
        {
            pw.printf("    public %sProxy %s(%s)\n", retType, methName, paramDecl);
            pw.printf("    {\n");
            pw.printf("        return new %sProxy(node.%s(%s));\n", retType, methName, paramImpl);
            pw.printf("    }\n");
            pw.printf("\n");
        }

    }

    private String capFirst(String value)
    {
        return "".equals(value) ? "" : (value.substring(0, 1).toUpperCase() + value.substring(1));
    }

    private String buildClassName(Element ele)
    {
        String sb = null;
        while (true)
        {
            if (sb == null)
            {
                sb = ele.getSimpleName().toString().replace('.', '$');
                ele = ele.getEnclosingElement();
            }
            else if (ele instanceof TypeElement)
            {
                sb = ele.getSimpleName().toString() + "$" + sb;
                ele = ele.getEnclosingElement();
            }
            else if (ele instanceof PackageElement)
            {
                return ele.toString() + "." + sb;
            }
            else
            {
                throw new RuntimeException("Illegal element " + ele);
            }
        }
    }

}
