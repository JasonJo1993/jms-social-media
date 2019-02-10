package com.jms.socialmedia.configuration;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Settings {

	private static final String DELIMITER = ",";
	
	private Settings() {
		throw new IllegalStateException();
	}
	
	public static Setting<String> stringSetting(String name) {
		return new StringSettingBuilder().setName(name).build();
	}
	
	public static Setting<String> requiredStringSetting(String name) {
		return new StringSettingBuilder().setName(name).isRequired().build();
	}
	
	public static Setting<String> stringSettingWithDefault(String name, String defaultValue) {
		return new StringSettingBuilder().setName(name).setDefaultValue(defaultValue).build();
	}

	public static Setting<Boolean> booleanSetting(String name) {
		return new BooleanSettingBuilder().setName(name).build();
	}
	
	public static Setting<Boolean> requiredBooleanSetting(String name) {
		return new BooleanSettingBuilder().setName(name).isRequired().build();
	}
	
	public static Setting<Boolean> booleanSettingWithDefault(String name, Boolean defaultValue) {
		return new BooleanSettingBuilder().setName(name).setDefaultValue(defaultValue).build();
	}

	public static Setting<Integer> integerSetting(String name) {
		return new IntegerSettingBuilder().setName(name).build();
	}
	
	public static Setting<Integer> requiredIntegerSetting(String name) {
		return new IntegerSettingBuilder().setName(name).isRequired().build();
	}
	
	public static Setting<Integer> integerSettingWithDefault(String name, Integer defaultValue) {
		return new IntegerSettingBuilder().setName(name).setDefaultValue(defaultValue).build();
	}

	public static Setting<Double> doubleSetting(String name) {
		return new DoubleSettingBuilder().setName(name).build();
	}
	
	public static Setting<Double> requiredDoubleSetting(String name) {
		return new DoubleSettingBuilder().setName(name).isRequired().build();
	}
	
	public static Setting<Double> doubleSettingWithDefault(String name, Double defaultValue) {
		return new DoubleSettingBuilder().setName(name).setDefaultValue(defaultValue).build();
	}

	public static Setting<List<String>> stringListSetting(String name) {
		return new StringListSettingBuilder().setName(name).build();
	}
	
	public static Setting<List<String>> requiredStringListSetting(String name) {
		return new StringListSettingBuilder().setName(name).isRequired().build();
	}
	
	public static Setting<List<String>> stringListWithDefault(String name, List<String> defaultValue) {
		return new StringListSettingBuilder().setName(name).setDefaultValue(defaultValue).build();
	}
	
	public static Setting<List<Integer>> integerListSetting(String name) {
		return new IntegerListSettingBuilder().setName(name).build();
	}
	
	public static Setting<List<Integer>> requiredIntegerListSetting(String name) {
		return new IntegerListSettingBuilder().setName(name).isRequired().build();
	}
	
	public static Setting<List<Integer>> stringIntegerWithDefault(String name, List<Integer> defaultValue) {
		return new IntegerListSettingBuilder().setName(name).setDefaultValue(defaultValue).build();
	}
	
	private static abstract class AbstractSetting<T> implements Setting<T> {
		protected final String name;
		protected final boolean required;
		protected final T defaultValue;
		
		protected AbstractSetting(AbstractSettingBuilder<T> builder) {
			this.name = checkNotNull(builder.name);
			this.required = builder.required;
			this.defaultValue = builder.defaultValue;
		}
		@Override
		public String name() {
			return name;
		}
		@Override
		public boolean isRequired() {
			return required;
		}
		@Override
		public T defaultValue() {
			return defaultValue;
		}
	}
	
	private static abstract class AbstractSettingBuilder<T> {
		String name;
		boolean required = false;
		T defaultValue;
		
		AbstractSettingBuilder<T> setName(String name) {
			this.name = name;
			return this;
		}
		AbstractSettingBuilder<T> isRequired() {
			required = true;
			return this;
		}
		AbstractSettingBuilder<T> setDefaultValue(T defaultValue) {
			this.defaultValue = defaultValue;
			return this;
		}
		abstract Setting<T> build();
	}
	
	private static class StringSetting extends AbstractSetting<String> {

		StringSetting(StringSettingBuilder builder) {
			super(builder);
		}
		@Override
		public String convertRawValue(String rawValue) {
			return rawValue.trim();
		}
	}
	
	private static class StringSettingBuilder extends AbstractSettingBuilder<String> {
		StringSetting build() {
			return new StringSetting(this);
		}
	}
	
	private static class BooleanSetting extends AbstractSetting<Boolean> {

		BooleanSetting(BooleanSettingBuilder builder) {
			super(builder);
		}
		@Override
		public Boolean convertRawValue(String rawValue) {
			if (rawValue == null) {
				return null;
			} else {
				return Boolean.parseBoolean(rawValue.trim());
			}
		}
	}
	
	private static class BooleanSettingBuilder extends AbstractSettingBuilder<Boolean> {
		BooleanSetting build() {
			return new BooleanSetting(this);
		}
	}
	
	private static class IntegerSetting extends AbstractSetting<Integer> {

		IntegerSetting(IntegerSettingBuilder builder) {
			super(builder);
		}

		@Override
		public Integer convertRawValue(String rawValue) {
			if (rawValue == null) {
				return null;
			} else {
				return Integer.parseInt(rawValue.trim());
			}
		}
	}
	
	private static class IntegerSettingBuilder extends AbstractSettingBuilder<Integer> {
		IntegerSetting build() {
			return new IntegerSetting(this);
		}
	}
	
	private static class DoubleSetting extends AbstractSetting<Double> {

		DoubleSetting(DoubleSettingBuilder builder) {
			super(builder);
		}

		@Override
		public Double convertRawValue(String rawValue) {
			if (rawValue == null) {
				return null;
			} else {
				return Double.parseDouble(rawValue.trim());
			}
		}
	}
	
	private static class DoubleSettingBuilder extends AbstractSettingBuilder<Double> {
		DoubleSetting build() {
			return new DoubleSetting(this);
		}
	}
	
	private static abstract class AbstractListSetting<T> extends AbstractSetting<List<T>> {
		
		AbstractListSetting(AbstractSettingBuilder<List<T>>  builder) {
			super(builder);
		}
		@Override
		public List<T> convertRawValue(String rawValue) {
			return Arrays.stream(rawValue.split(DELIMITER)).map(this::convertListItem).collect(Collectors.toList());
		}
		
		protected abstract T convertListItem(String item);
	}

	private static class StringListSetting extends AbstractListSetting<String> {

		StringListSetting(StringListSettingBuilder builder) {
			super(builder);
		}
		@Override
		protected String convertListItem(String item) {
			return item.trim();
		}

	}
	
	private static class StringListSettingBuilder extends AbstractSettingBuilder<List<String>>{
		StringListSetting build() {
			return new StringListSetting(this);
		}
	}
	
	private static class IntegerListSetting extends AbstractListSetting<Integer> {

		IntegerListSetting(IntegerListSettingBuilder builder) {
			super(builder);
		}
		@Override
		protected Integer convertListItem(String item) {
			return Integer.parseInt(item.trim());
		}

	}
	
	private static class IntegerListSettingBuilder extends AbstractSettingBuilder<List<Integer>>{
		IntegerListSetting build() {
			return new IntegerListSetting(this);
		}
	}
}