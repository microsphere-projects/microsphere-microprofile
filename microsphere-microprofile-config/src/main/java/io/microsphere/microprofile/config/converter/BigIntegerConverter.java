package io.microsphere.microprofile.config.converter;

import java.math.BigInteger;

public class BigIntegerConverter extends AbstractConverter<BigInteger> {

    @Override
    protected BigInteger doConvert(String value) {
        return new BigInteger(value);
    }
}
