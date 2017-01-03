package study.heaven7.com.android_study.util;

import android.text.TextUtils;

import com.pinyinsearch.model.PinyinSearchUnit;
import com.pinyinsearch.util.PinyinUtil;
import com.pinyinsearch.util.QwertyUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PinyinSearchUtil{

        /*private static final Comparator<Object> sChineseComparator = Collator.getInstance(Locale.CHINA);

        public static final Comparator<IPinyinSearchBean> sAscComparator = new Comparator<IPinyinSearchBean>() {
            public int compare(IPinyinSearchBean lhs, IPinyinSearchBean rhs) {
                return sChineseComparator.compare(lhs.getPinyinSearchHelper().getSortKey(), rhs.getPinyinSearchHelper().getSortKey());
            }
        };
        public static final Comparator<IPinyinSearchBean> sDescComparator = new Comparator<IPinyinSearchBean>() {
            public int compare(IPinyinSearchBean lhs, IPinyinSearchBean rhs) {
                return sChineseComparator.compare(rhs.getPinyinSearchHelper().getSortKey(), lhs.getPinyinSearchHelper().getSortKey());
            }
        };*/
        public static final Comparator<IPinyinSearchBean> sSearchComparator = new Comparator<IPinyinSearchBean>() {
            public int compare(IPinyinSearchBean lhs, IPinyinSearchBean rhs) {
                int compareMatchStartIndex = (lhs.getPinyinSearchHelper().getMatchStartIndex() - rhs.getPinyinSearchHelper().getMatchStartIndex());
                return ((0!=compareMatchStartIndex) ? (compareMatchStartIndex) : (rhs.getPinyinSearchHelper().getMatchLength()
                        - lhs.getPinyinSearchHelper().getMatchLength()));
            }
        };

        /**
         *  qwery pinyin search
         * @param keyword the keyword
         * @param rawList the list data
         * @param callback the callback
         * @param <T> the t extends IPinyinSearchBean
         */
        public static <T extends PinyinSearchUtil.IPinyinSearchBean> void  qwertySearch(String keyword, List<T> rawList,
                                         ISearchCallback<T> callback){
            if(TextUtils.isEmpty(keyword)){
                for(int i=0,size = rawList.size() ;i<size ;i++ ){
                    rawList.get(i).getPinyinSearchHelper().reset();
                }
                callback.onEmptySearchContent(rawList);
            }else{
                List<T> mSearchList = new ArrayList<>();
                for(int i=0,size = rawList.size()  ;i < size ;i++){
                    final T bean = rawList.get(i);
                    if(bean.getPinyinSearchHelper().match(keyword)){
                        mSearchList.add(bean);
                    }
                }
                Collections.sort(mSearchList, sSearchComparator);
                callback.onSearchResult(mSearchList);
            }
        }

        public static PinyinSearchHelper create(String name){
            return new PinyinSearchHelper(name);
        }

        private static String praseSortKey(String sortKey) {
            if (null == sortKey || sortKey.length() <= 0) {
                return null;
            }
            if ((sortKey.charAt(0) >= 'a' && sortKey.charAt(0) <= 'z')
                    || (sortKey.charAt(0) >= 'A' && sortKey.charAt(0) <= 'Z')) {
                return sortKey;
            }
            return "#"+ sortKey;
        }

        /**
         * the all search bean must implement this
         */
        public  interface IPinyinSearchBean{

            PinyinSearchHelper getPinyinSearchHelper();

        }

        /**
         * the search callback
         */
        public  interface ISearchCallback< T extends IPinyinSearchBean>{

            /**
             *  callback when the search content ( keyword ) is empty.
             * @param rawList the raw search bean list
             */
            void onEmptySearchContent(List<T> rawList);

            /**
             *  called on search result
             * @param mSearchList the search result list
             */
            void onSearchResult(List<T> mSearchList);
        }

        /**
         * implement with qwery arithmetic.
         */
        public static class PinyinSearchHelper{
            private String mUsername;
            private int mMatchStartIndex;
            private int mMatchLength;         //match length
            private String mSortKey;

            private final PinyinSearchUnit mNamePinyinSearchUnit;
            private final StringBuilder mMatchKeywords;


            public PinyinSearchHelper(String name) {
                this.mUsername = name;
                this.mNamePinyinSearchUnit = new PinyinSearchUnit(name);
                this.mMatchStartIndex = -1;
                this.mMatchLength = 0;
                this.mMatchKeywords = new StringBuilder();

                PinyinUtil.parse(mNamePinyinSearchUnit);
                mSortKey = praseSortKey(PinyinUtil.getSortKey(mNamePinyinSearchUnit).toUpperCase());
            }

            /**
             * reset the matches.
             */
            public void reset(){
                clearMatchKeywords();
                setMatchLength(0);
                setMatchStartIndex(-1);
            }

            /**
             *  true to match the keyword,false otherwise .
             * @param keyword the key word to check match
             * @return   true to match the keyword,false otherwise .
             */
            public boolean match(String keyword){
                if(QwertyUtil.match(getSearchPinyinUnit(), keyword)){
                    setMatchKeywords(getSearchPinyinUnit().getMatchKeyword().toString());
                    setMatchStartIndex(getName().indexOf(getMatchKeywords().toString()));
                    setMatchLength(getMatchKeywords().length());
                    return true;
                }
                return false;
            }

            /**
             * @return  the match start index
             */
            public int getMatchStartIndex(){
                return mMatchStartIndex;
            }
            public void setMatchStartIndex(int index){
                mMatchStartIndex = index;
            }
            public void setMatchLength(int length){
                mMatchLength = length;
            }
            public int getMatchLength(){
                return mMatchLength;
            }

            public StringBuilder getMatchKeywords() {
                return mMatchKeywords;
            }

            public void setMatchKeywords(String matchKeywords) {
                mMatchKeywords.delete(0, mMatchKeywords.length());
                mMatchKeywords.append(matchKeywords);
            }
            public void clearMatchKeywords() {
                mMatchKeywords.delete(0, mMatchKeywords.length());
            }

            public String getSortKey() {
                return mSortKey;
            }

            public PinyinSearchUnit getSearchPinyinUnit() {
                return mNamePinyinSearchUnit;
            }

            public String getName() {
                return mUsername;
            }
        }
    }
