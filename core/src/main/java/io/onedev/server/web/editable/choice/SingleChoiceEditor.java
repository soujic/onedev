package io.onedev.server.web.editable.choice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;
import org.json.JSONException;
import org.json.JSONWriter;

import com.google.common.base.Preconditions;

import io.onedev.server.util.OneContext;
import io.onedev.server.web.component.select2.ChoiceProvider;
import io.onedev.server.web.component.select2.Response;
import io.onedev.server.web.component.select2.Select2Choice;
import io.onedev.server.web.editable.ErrorContext;
import io.onedev.server.web.editable.PathSegment;
import io.onedev.server.web.editable.PropertyDescriptor;
import io.onedev.server.web.editable.PropertyEditor;
import io.onedev.utils.ReflectionUtils;

@SuppressWarnings("serial")
public class SingleChoiceEditor extends PropertyEditor<String> {

	private List<String> choices = new ArrayList<>();
	
	private Select2Choice<String> input;
	
	public SingleChoiceEditor(String id, PropertyDescriptor propertyDescriptor, IModel<String> propertyModel) {
		super(id, propertyDescriptor, propertyModel);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		OneContext oneContext = new OneContext(this);
		
		OneContext.push(oneContext);
		try {
			io.onedev.server.util.editable.annotation.ChoiceProvider choiceProvider = 
					propertyDescriptor.getPropertyGetter().getAnnotation(
							io.onedev.server.util.editable.annotation.ChoiceProvider.class);
			Preconditions.checkNotNull(choiceProvider);
			for (String each: (List<String>)ReflectionUtils
					.invokeStaticMethod(propertyDescriptor.getBeanClass(), choiceProvider.value())) {
				choices.add(each);
			}
		} finally {
			OneContext.pop();
		}
		
        input = new Select2Choice<String>("input", Model.of(getModelObject()), new ChoiceProvider<String>() {

			@Override
			public void query(String term, int page, Response<String> response) {
				response.setResults(choices);
				response.setHasMore(false);
			}

			@Override
			public void toJson(String choice, JSONWriter writer) throws JSONException {
				writer.key("id").value(StringEscapeUtils.escapeHtml4(choice));
			}

			@Override
			public Collection<String> toChoices(Collection<String> ids) {
				return ids;
			}
        	
        }) {

        	@Override
        	protected void onInitialize() {
        		super.onInitialize();
        		getSettings().setPlaceholder("Select below...");
        		getSettings().setFormatResult("onedev.server.choiceFormatter.formatResult");
        		getSettings().setFormatSelection("onedev.server.choiceFormatter.formatSelection");
        		getSettings().setEscapeMarkup("onedev.server.choiceFormatter.escapeMarkup");
        		
        		setConvertEmptyInputStringToNull(true);
        	}
        	
        };
        
        input.setLabel(Model.of(getPropertyDescriptor().getDisplayName(this)));

		input.add(new AjaxFormComponentUpdatingBehavior("change"){

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				onPropertyUpdating(target);
			}
			
		});
		
		add(input);
	}

	@Override
	public ErrorContext getErrorContext(PathSegment pathSegment) {
		return null;
	}

	@Override
	protected String convertInputToValue() throws ConversionException {
		return input.getConvertedInput();
	}

}
