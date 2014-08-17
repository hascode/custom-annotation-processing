package com.hascode.tutorial.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.hascode.tutorial.annotation.Dao;

@SupportedAnnotationTypes({ "com.hascode.tutorial.annotation.Dao" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DaoProcessor extends AbstractProcessor {

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		Messager messager = processingEnv.getMessager();
		Filer filer = processingEnv.getFiler();

		for (TypeElement te : annotations) {
			for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
				String fqEntityClassName = null;
				Element entityElement = null;
				try {
					fqEntityClassName = e.getAnnotation(Dao.class).entity().getName();
				} catch (MirroredTypeException mte) {
					TypeMirror typeMirror = mte.getTypeMirror();
					entityElement = processingEnv.getTypeUtils().asElement(typeMirror);
					fqEntityClassName = entityElement.toString();
				}
				String fqIdClassName = null;
				try {
					fqIdClassName = e.getAnnotation(Dao.class).idClass().getName();
				} catch (MirroredTypeException mte) {
					TypeMirror typeMirror = mte.getTypeMirror();
					fqIdClassName = processingEnv.getTypeUtils().asElement(typeMirror).toString();
				}
				messager.printMessage(Diagnostic.Kind.NOTE, "dao annotation on type " + e.toString() + " found. entity is " + fqEntityClassName + " and has an id of type: " + fqIdClassName);
				try {
					JavaFileObject f = processingEnv.getFiler().createSourceFile(fqEntityClassName + "Dao");
					processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Creating " + f.toUri());
					Writer w = f.openWriter();
					try {
						PrintWriter pw = new PrintWriter(w);
						pw.println("package com.hascode.tutorial.app;");
						pw.println("public class BookDao {");
						pw.println("    public void print() {");
						pw.println("        System.out.println(\"Helo world!\");");
						pw.println("    }");
						pw.println("}");
						pw.flush();
					} finally {
						w.close();
					}
				} catch (IOException x) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, x.toString());
				}
			}
		}
		return true;
	}
}
