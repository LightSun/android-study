package study.heaven7.com.android_study.help;

/**
 * the default implements while retrack occoured.
 * Created by heaven7 on 2016/5/4.
 */
public class DefaultRetrackProcessor extends TagTracker.AbstractRetrackProcessor{
    @Override
    protected boolean process(ITrackManager detacher, TagTracker.TagNode node) {
        detacher.detachNodesForTarget(node);
        detacher.attachNode(node);
        return true;
    }
}
