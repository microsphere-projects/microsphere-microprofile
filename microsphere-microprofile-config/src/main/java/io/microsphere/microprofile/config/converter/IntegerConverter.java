package io.microsphere.microprofile.config.converter;

public class IntegerConverter extends AbstractConverter<Integer> {

    @Override
    protected Integer doConvert(String value) {
        return Integer.valueOf(value);
    }
}
