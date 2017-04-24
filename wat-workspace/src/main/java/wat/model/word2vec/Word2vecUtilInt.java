package wat.model.word2vec;

import wat.model.BaseModelInt;

public interface Word2vecUtilInt extends BaseModelInt {

    void resetWord2vecParams();

    Word2vecTrainingParams getWord2vecParams();

}
