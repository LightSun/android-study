package com.tt;

/**
 * Created by cj on 2016/12/27.
 */

public class CoreType {

//    请求的内核类型(旧版)
//    en.sent.score         英文句子评测
//    en.word.score        英文单词评测
//    en.sent.rec             英文句子识别
//    en.grammar           语法题
//    en.open                  开放题
//    en.pred.score         段落评测

//    请求的内核类型(新版)
//    sent.eval     英文句子评测    传音频和单词文本，返回单词得分
//    word.eval     英文单词评测    传音频和句子文本，返回句子得分
//    para.eval     离线版段落评测
//    choice.rec    英文句子识别    传音频和多个句子文本(用竖线｜隔开)，返回音频中读到的句子和得分
//    asr.rec       英文自由识别    传音频，返回识别出的文本
//    grammar.rec   语法题
//    open.eval     开放题（考试技术：段落朗读、情景问答、口头作文、看图说话、故事复述、句子翻译等）

    public static final String EN_SENT_EVAL = "sent.eval";
    public static final String EN_WORD_EVAL = "word.eval";
    public static final String EN_CHOICE_REC = "choice.rec";
    public static final String EN_GRAMMAR_REC = "grammar.rec";
    public static final String EN_OPEN_EVAL = "open.eval";
    public static final String EN_PARA_EVAL = "para.eval";
    public static final String EN_ASR_REC = "asr.rec";
}
