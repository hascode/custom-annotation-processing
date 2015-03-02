package com.hascode.tutorial.processor;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.hascode.tutorial.annotation.Dao;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

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
				String daoSuffix = e.getAnnotation(Dao.class).daoSuffix();
				messager.printMessage(Diagnostic.Kind.NOTE, "dao annotation on type " + e.toString() + " found. entity is " + fqEntityClassName + " and the designated suffix is: " + daoSuffix);
				try {
					TypeSpec dao = createDao("BookDao");
					JavaFile javaFile = JavaFile.builder("com.hascode.tutorial.app", dao).build();
					javaFile.writeTo(filer);
				} catch (IOException x) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, x.toString());
				}
			}
		}
		return true;
	}

	private TypeSpec createDao(final String className) {
		return TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC).addMethod(createMethod("create")).addMethod(createMethod("read")).addMethod(createMethod("update"))
				.addMethod(createMethod("delete")).build();
	}

	private MethodSpec createMethod(final String name) {
		return MethodSpec.methodBuilder(name).addModifiers(Modifier.PUBLIC).addStatement("$T.out.println($S)", System.class, "Generated method.").returns(void.class).build();
	}
}
