
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2019, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.preprocessing.filter;

import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.util.attrs.AttrBoolean;

import static org.carrot2.text.analysis.TokenTypeUtils.isCommon;

/**
 * Accepts words that are not stop words and phrases that do not start nor end in a stop
 * word.
 */
public class StopWordLabelFilter extends SingleLabelFilterBase {
  /**
   * Remove leading and trailing stop words. Removes labels that consist of, start or
   * end in stop words.
   */
  public AttrBoolean enabled = attributes.register("enabled", AttrBoolean.builder()
      .label("Remove leading and trailing stop words")
      .defaultValue(true)
      .build());

  @Override
  public boolean acceptPhrase(PreprocessingContext context, int phraseIndex) {
    final int[] wordIndices = context.allPhrases.wordIndices[phraseIndex];
    final short[] termTypes = context.allWords.type;

    return !isCommon(termTypes[wordIndices[0]])
        && !isCommon(termTypes[wordIndices[wordIndices.length - 1]]);
  }

  @Override
  public boolean acceptWord(PreprocessingContext context, int wordIndex) {
    return !isCommon(context.allWords.type[wordIndex]);
  }

  public boolean isEnabled() {
    return enabled.get();
  }
}
