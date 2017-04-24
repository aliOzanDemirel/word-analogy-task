package wat.calculator;

import wat.exceptions.ModelBuildException;

import java.util.List;

public interface GloveCalculatorInt extends AccuracyCalculatorInt {

    void createGlove(int corpusType) throws ModelBuildException;


}
