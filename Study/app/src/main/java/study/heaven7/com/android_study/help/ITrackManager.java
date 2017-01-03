package study.heaven7.com.android_study.help;

/**
 * the track provider
 * Created by heaven7 on 2016/5/4.
 */
public interface ITrackManager {

    /**
     * detach the nodes ( that was tracked previous ) for the target node
     * @param node the target
     */
    void detachNodesForTarget(TagTracker.TagNode node);

    void attachNode(TagTracker.TagNode node);

}
