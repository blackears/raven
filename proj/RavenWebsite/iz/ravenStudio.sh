#!/bin/sh
CLASSPATH=../lib/jmf/lib/*;$CLASSPATH
CLASSPATH=../lib/jogamp/jar/gluegen-rt.jar;$CLASSPATH
CLASSPATH=../lib/jogamp/jar/jogl.all.jar;$CLASSPATH
CLASSPATH=../lib/jpen/*;$CLASSPATH
CLASSPATH=../lib/raven/*;$CLASSPATH
LIBPATH=../lib/jpen;../lib/jogamp/lib
java -Xms32m -Xmx1024m -cp "$CLASSPATH" -Djava.library.path="$LIBPATH" com.kitfox.raven.editor.app.RavenEditorMain
