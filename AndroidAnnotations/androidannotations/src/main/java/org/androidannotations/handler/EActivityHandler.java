/**
 * Copyright (C) 2010-2014 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.handler;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.helper.AnnotationHelper;
import org.androidannotations.helper.IdValidatorHelper;
import org.androidannotations.holder.EActivityHolder;
import org.androidannotations.model.AnnotationElements;
import org.androidannotations.process.IsValid;
import org.androidannotations.process.ProcessHolder;
import org.androidannotations.rclass.IRClass;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JMethod;

public class EActivityHandler extends BaseGeneratingAnnotationHandler<EActivityHolder> {

	private AnnotationHelper annotationHelper;

	public EActivityHandler(ProcessingEnvironment processingEnvironment) {
		super(EActivity.class, processingEnvironment);
		annotationHelper = new AnnotationHelper(processingEnv);
	}

	@Override
	public EActivityHolder createGeneratedClassHolder(ProcessHolder processHolder, TypeElement annotatedElement) throws Exception {
		return new EActivityHolder(processHolder, annotatedElement, androidManifest);
	}

	@Override
	public void validate(Element element, AnnotationElements validatedElements, IsValid valid) {
		super.validate(element, validatedElements, valid);

		validatorHelper.extendsActivity(element, valid);

		validatorHelper.resIdsExist(element, IRClass.Res.LAYOUT, IdValidatorHelper.FallbackStrategy.ALLOW_NO_RES_ID, valid);

		validatorHelper.componentRegistered(element, androidManifest, valid);
	}

	@Override
	public void process(Element element, EActivityHolder holder) {

		List<JFieldRef> fieldRefs = annotationHelper.extractAnnotationFieldRefs(processHolder, element, getTarget(), rClass.get(IRClass.Res.LAYOUT), false);

		JFieldRef contentViewId = null;
		if (fieldRefs.size() == 1) {
			contentViewId = fieldRefs.get(0);
		}

		if (contentViewId != null) {
			JBlock onCreateBody = holder.getOnCreate().body();
			JMethod setContentView = holder.getSetContentViewLayout();
			onCreateBody.invoke(setContentView).arg(contentViewId);
		}
	}
}
