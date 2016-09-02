package com.objectivetruth.uoitlibrarybooking.data.models.common;
public class ParseUtilities {
    /**
     * Takes {@code original} String and returns the string between the 2 search terms, {@code beginningSearchTerm}
     * and {@code endSearchTerm}. Will story searching at the first occurance. Example:
     * original: aaaHELLOacWORLDccc
     * beginningSearchTerm: aaa
     * endSearchTerm: ccc
     * returns: HELLOacWORLD
     * @param original
     * @param beginingSearchTerm
     * @param endSearchTerm
     * @return
     */
    static public String findStringFromStringBetweenSearchTerms(String original,
                                                                  String beginingSearchTerm,
                                                                  String endSearchTerm) {
        int offsetOfBeginningSearchTerm = beginingSearchTerm.length();

        int startSearchResult = original.indexOf(beginingSearchTerm);
        int endSearchResult = original.indexOf(endSearchTerm, startSearchResult + offsetOfBeginningSearchTerm);

        int startOfResultString = startSearchResult + offsetOfBeginningSearchTerm;

        return (String) original.subSequence(startOfResultString, endSearchResult);
    }

}
