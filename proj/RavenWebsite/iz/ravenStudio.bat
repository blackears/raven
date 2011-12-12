@echo off
set BASE=%~dp0..\lib

set CLASSPATH=%BASE%/jmf/lib/*;%CLASSPATH%
set CLASSPATH=%BASE%/jogamp/jar/gluegen-rt.jar;%CLASSPATH%
set CLASSPATH=%BASE%/jogamp/jar/jogl.all.jar;%CLASSPATH%
set CLASSPATH=%BASE%/jpen/*;%CLASSPATH%
set CLASSPATH=%BASE%/raven/*;%CLASSPATH%
set LIBPATH=%BASE%/jpen;%BASE%/jogamp/lib

java -Xms32m -Xmx1024m -cp "%CLASSPATH%" -Djava.library.path="%LIBPATH%" com.kitfox.raven.editor.app.RavenEditorMain

