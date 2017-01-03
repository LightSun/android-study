package study.heaven7.com.android_study.help;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import study.heaven7.com.android_study.util.Cacher;

/**
 * track an order mNodes. help we fast track the care action. {@link CareAction}.
 * <li>this class is thread safe.</li>
 * <li>you must careful of the {@link TagNode}'s level , while some previous mNodes level >= new node's level,they will auto untrack </li>
 * <p>usage see /${project}/temp/TagTrackerTest.java</p>
 * @author heaven7
 */
public final class TagTracker implements ITrackManager {

	private static final boolean DEBUG = true;
	private static final Cacher<TagNode,Void> POOL = new Cacher<TagNode,Void>(20){
		@Override
		public TagNode create(Void p) {
			return new TagNode();
		}
		protected void onRecycleSuccess(TagTracker.TagNode t) {
			t.extra = null;
		}
	};

	public final List<TagNode> mNodes;
	private final List<IReportCallback> mCallbacks;
	private List<CareAction> mCareActions;

	private final List<CareAction> mReportedActions;

	private final CareAction mTempAction;
	private final List<TagNode> mTempNodes;
	private final TagNode mLastNode;

	private AbstractRetrackProcessor mProcessor;

	private TagTracker() {
		super();
		mNodes = new ArrayList<>();
		mCallbacks = new ArrayList<>(3);
		mCareActions = new ArrayList<>();

		mReportedActions = new ArrayList<>();
		mTempAction = new CareAction();
		mTempNodes = new ArrayList<>();
		mLastNode = new TagNode();
	}

	public static TagTracker getInstance(){
		return Creator.INSTANCE;
	}

	/**
	 * @return the last operate node.
     */
	public TagNode getLastOperateNode(){
		return mLastNode;
	}

	public void setRetrackProcessor(AbstractRetrackProcessor processor){
         if(processor == null){
			throw  new NullPointerException();
		}
		processor.setTrackDetacher(this);
		this.mProcessor = processor;
	}
	/**
	 * add a action to care
	 * @param action the action
	 */
	public void addCareAction(CareAction action) {
		if(action.mNodes.size()==0){
			throw new IllegalStateException("CareAction must have the care list mNodes !");
		}
		mCareActions.add(action);
	}

	/**
	 * add a report callback
	 */
	public void addCallback(IReportCallback callback){
		if(callback == null){
			throw new NullPointerException();
		}
		mCallbacks.add(callback);
	}

	/**  remove a callback  */
	public void removeCallback(IReportCallback callback){
		mCallbacks.remove(callback);
	}

	/**
	 * untrack the all mNodes.
	 */
	public synchronized void untrackAll(){
		final List<TagNode> mNodes = this.mNodes;
		for(int i=0,size = mNodes.size() ; i<size ;i++){
			POOL.recycle(mNodes.get(i));
		}
		mNodes.clear();
	}

	/**
	 * track the id with tagName of node.
	 * @param tagName the tag of this track
	 * @param level  the level of this track
	 */
	public void track(int level ,String tagName){
		track(TagNode.obtain(level,tagName));
	}
	/**
	 * track the id with tagName of node. and carry extra data.
	 * @param tagName the tag of this track
	 * @param level  the level of this track
	 * @param  extra the extra data.
	 */
	public void track(int level ,String tagName,Object extra){
		track(TagNode.obtain(level,tagName,extra));
	}
	/**
	 * track the id with tagName of the event node. and carry extra data.
	 * @param tag the tag of this track
	 * @param level  the level of this track
	 * @param  extra the extra data. will not participate the 'equals' method.
	 */
	public void trackEvent(int level,String tag, Object extra){
		trackEvent(TagNode.obtain(level,tag, extra));
	}
	/**
	 * track the id with tagName of the event node..
	 * @param tag the tag of this track
	 * @param level  the level of this track
	 */
	public void trackEvent(int level,String tag){
		trackEvent(TagNode.obtain(level,tag));
	}
	/**
	 * track a event node just use once
	 */
	public void trackEvent(TagNode node){
		trackInternal(node,true);
	}
	/**
	 * track a node , you should call untrack while the page switched.
	 */
	public  void track(TagNode node){
		trackInternal(node,false);
	}

	/**
	 * track the target node. and check the level if need untrack.
	 * @param node the target
	 * @param event is event
     */
	private synchronized void trackInternal(TagNode node,boolean event){
		if(mNodes.contains(node)){
			if(mProcessor == null || !mProcessor.process(node)){
				System.err.println("the node already exists. " + node);
			}
			return;
		}
		//check level if old node >= while new node's level,that means it will be auto untrack
		detachNodesForTarget(node);

		final List<TagNode> mTempNodes = this.mTempNodes;
		final List<TagNode> mNodes = this.mNodes;

		mNodes.add(node);
		mTempNodes.addAll(mNodes);

		final boolean addToReportList = !event;
		TagNode tmp;
		int index;
		CareAction.INodeComparetor nodeComparetor;
        //match action
		for(CareAction action : mCareActions){
			nodeComparetor = action.getNodeComparetor();
			switch (action.getCareMode()){
				case CareAction.MODE_START:
					if (isFullList(mNodes, action.mNodes, nodeComparetor)) {
						dispatchCallbackonCareActionOccoured(mTempNodes,action, false, true);
					}
					break;

				case CareAction.MODE_FULL :
				case CareAction.MODE_END : {
					tmp = action.mNodes.get(0);
					index = -1;
					for(int i = 0,size = mNodes.size(); i<size ;i++){
						if(nodeComparetor.equals(mNodes.get(i),tmp)){
							index = i;
							break;
						}
					}
					if (index == -1) {
						continue;
					}
					if (isFullList(mNodes.subList(index, mNodes.size()), action.mNodes, nodeComparetor)) {
						dispatchCallbackonCareActionOccoured(mTempNodes,action, addToReportList, false);
					}
				}
					break;
				default: throw  new RuntimeException();
			}
		}
		mTempNodes.clear();

		//event is once node. so need delete
		if(event){
			POOL.recycle(mNodes.remove(mNodes.size()-1));
		}else{
			mLastNode.copyFrom(node);
		}
	}

	@Override
	public void detachNodesForTarget(TagNode node) {
		final List<TagNode> mTempNodes = this.mTempNodes;
		mTempNodes.addAll(this.mNodes);
		TagNode tmp;
		for(int i=0,size = mTempNodes.size() ; i<size ;i++){
			tmp = mTempNodes.get(i);
			if(tmp.level >= node.level){
				untrackImpl(tmp);
				logDebug("detachNodesForTarget", "the node("+tmp+") was detached for target("+node+").");
			}
		}
		mTempNodes.clear();
	}

	@Override
	public void attachNode(TagNode node) {
		this.mNodes.add(node);
		logDebug("attachNode","the track stack is : " + mNodes.toString());
	}

	private boolean untrackImpl(TagNode node){
		int index = mNodes.indexOf(node);
		if(index < 0){
			return false;
		}
		//remove from report list if need.
		ListIterator<CareAction> it = mReportedActions.listIterator();

		CareAction action;
		boolean remove;
		while(it.hasNext()){
			remove = false;
			action = it.next();
			for(int i = 0, size = action.getCareNodes().size(); i<size ; i++ ){
                if(action.getNodeComparetor().equals(action.getCareNodes().get(i),node )){
					remove = true;
					break;
				}
			}
			if(remove){
				it.remove();
				logDebug("untrackImpl", " the action is removed from report list. action = "+ action +" ,node = " + node);
			}
		}
		//remove and recycle
		mNodes.remove(index);
		POOL.recycle(node);
		return true;
	}

	private static void logDebug(String method, String msg) {
		if(DEBUG) {
			System.out.println("called [ " + method + "() ]: " + msg);
		}
	}

	/** dispatch the callback
	 * @param mTempNodes the temp nodes of the current tracker stack. will be clear after callback.
	 * @param action the care action.
	 * @param addToReportList  whether  add the action to the report list or not.
	 * @param canRepeatReport  whether  can repeat the action to the report list or not.   */
	private void dispatchCallbackonCareActionOccoured(List<TagNode> mTempNodes, CareAction action,
													  boolean addToReportList, boolean canRepeatReport) {
		if(!canRepeatReport && mReportedActions.contains(action)){
			return ;
		}
		if(addToReportList) {
			mReportedActions.add(action);
		}
		mTempAction.copyFrom(action);
		final CareAction tmp = mTempAction;
		for(IReportCallback callback : mCallbacks){
			callback.onReportCareAction(mTempNodes, tmp);
		}
	}
	/** is src contains the all mNodes of target and must in order。*/
	private static boolean isFullList(List<TagNode> src, List<TagNode> target,CareAction.INodeComparetor comparetor) {
		if(src.size() < target.size()){
			return false;
		}
		for(int i= 0 , size = target.size() ; i< size ;i++){
			if(!comparetor.equals(src.get(i), target.get(i) )){
				return false ;
			}
		}
		return true;
	}

	private static class Creator{
		public static final TagTracker INSTANCE = new TagTracker();
	}

	/**
	 * the care action. contains a list mNodes, and must in order.
	 */
	public static class CareAction{
		/**
		 * the start mode .indicate the mNodes is the start.
		 *  that means the action will be callback all the time if action matched..
		 */
		public static final byte MODE_START   = 1;
		public static final byte MODE_END     = 2;
		public static final byte MODE_FULL    = 3;

		private static final INodeComparetor DEFAULT_COMPARETOR = new INodeComparetor() {
			@Override
			public boolean equals(TagNode node1, TagNode node2) {
				return node1.equals(node2);
			}
		};
		/**
		 * the care nodes in order
		 */
		final List<TagNode> mNodes;
		/** the care mode . default is full */
		byte mCareMode = MODE_FULL;

		INodeComparetor mComparetor = DEFAULT_COMPARETOR;

		@IntDef({MODE_START, MODE_END, MODE_FULL })
		@Retention(RetentionPolicy.SOURCE)
		public @interface CareMode{
		}

		/**
		 * the node comparetor
		 */
		public interface INodeComparetor{
             boolean equals(TagNode node1, TagNode node2);
		}

		public CareAction() {
			super();
			mNodes = new ArrayList<>(7);
		}
		public CareAction addCareNode(TagNode node){
			mNodes.add(node);
			return this;
		}
		public CareAction addCareNode(int level ,String tagName){
			return addCareNode(TagNode.obtain(level,tagName));
		}
		public List<TagNode> getCareNodes() {
			return mNodes;
		}
		/** return the care mode default is full */
		public byte getCareMode(){
			return mCareMode;
		}
		public void setCareMode(@CareMode byte careMode){
			this.mCareMode = careMode;
		}

		public INodeComparetor getNodeComparetor() {
			return mComparetor;
		}
		public void setNodeComparetor(INodeComparetor comparetor) {
			if(comparetor == null){
				throw  new NullPointerException();
			}
			this.mComparetor = comparetor;
		}

		public void copyFrom(CareAction action){
			this.mNodes.clear();
			this.mNodes.addAll(action.getCareNodes());
			this.mCareMode = action.mCareMode;
			this.mComparetor = action.mComparetor;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj == this)
				return true;
			if(! (obj instanceof CareAction)){
				return false;
			}
			CareAction tmp = (CareAction) obj;
			if(this.getCareMode() != tmp.getCareMode() || !this.getNodeComparetor().equals(tmp.getNodeComparetor())){
               return false;
			}
			//only mode and comparetor equals. can go below.
			final List<TagNode> list1 = this.getCareNodes();
			final List<TagNode> list2 = tmp.getCareNodes();
			if(list1.size() != list2.size()){
				return false;
			}
			final INodeComparetor comparetor = getNodeComparetor();
			for(int i=0,size = list1.size()  ; i<size ; i++){
				if(!comparetor.equals(list1.get(i),list2.get(i))){
					return false;
				}
			}
			return true;
		}

		@Override
		public String toString() {
			return "CareAction{" +
					"mNodes=" + mNodes +
					", mCareMode=" + mCareMode +
					", mComparetor=" + mComparetor +
					'}';
		}
	}

	/**
	 * the care action builder.
	 */
	public static class CareActionBuilder{

		private final CareAction mAction = new CareAction();

		public CareActionBuilder addCareNode(TagNode node){
			mAction.addCareNode(node);
			return this;
		}
		public CareActionBuilder addCareNode(int level ,String tagName){
			addCareNode(TagNode.obtain(level,tagName));
			return this;
		}

		public CareActionBuilder setCareMode(@CareAction.CareMode byte mode){
			mAction.setCareMode(mode);
			return this;
		}
		public CareActionBuilder setNodeComparetor(CareAction.INodeComparetor comparetor){
			mAction.setNodeComparetor(comparetor);
			return this;
		}

		public CareAction build(){
			return mAction;
		}
	}

	/**
	 * the tag node indicate a node of a track. if you want to track an order of multi mNodes. {@link CareAction}
	 * <li>equals two TagNodes return true means the node's level and tagName is the same.
	 * <li>the extra data don't participate the method 'equals'.
	 */
	public static class TagNode{

		/** the level decide whether to untrack or not, switch same whether means untrack success. */
		int level;        //level
		String tagName;
		Object extra;

		TagNode() {
			super();
		}
		public void copyFrom(TagNode src) {
			this.level = src.level;
			this.tagName = src.tagName;
			this.extra = src.extra;
		}
		public void copyTo(TagNode target){
			target.level = this.level;
			target.tagName = this.tagName;
			target.extra = this.extra;
		}

		void set(int level, String tagName) {
			this.level = level;
			this.tagName = tagName;
		}

		void set(int level, String tagName, Object extra) {
			this.level = level;
			this.tagName = tagName;
			this.extra = extra;
		}

		public Object getExtra(){
			return extra;
		}
		public int getLevel(){
			return level;
		}
		public String getTagName(){
			return tagName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + level;
			result = prime * result
					+ ((tagName == null) ? 0 : tagName.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TagNode other = (TagNode) obj;
			if (level != other.level)
				return false;
			if (tagName == null) {
				if (other.tagName != null)
					return false;
			} else if (!tagName.equals(other.tagName))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "TagNode{" +
					"level=" + level +
					", tagName='" + tagName + '\'' +
					", extra=" + extra +
					'}';
		}

		public static TagNode obtain(int level, String tagName){
			TagNode node = POOL.obtain();
			node.set(level, tagName);
			return node;
		}

		public static TagNode obtain(int level, String tagName,Object extra){
			TagNode node = POOL.obtain();
			node.set(level, tagName,extra);
			return node;
		}
	}

	/**
	 * the report callback
	 * @author heaven7
	 */
	public interface IReportCallback{

		/**
		 * called when the care action is occoured.
		 * @param mTempNodes the temp nodes in the TagTracker's stack.
		 * @param action the care action
		 */
		void onReportCareAction(List<TagNode> mTempNodes, CareAction action);

	}

	/**
	 * the retrack processor.
	 */
	public static abstract class AbstractRetrackProcessor {

		private ITrackManager mTrackDetacher;

		ITrackManager getTrackDetacher() {
			return mTrackDetacher;
		}
		void setTrackDetacher(ITrackManager mTrackDetacher) {
			this.mTrackDetacher = mTrackDetacher;
		}

		boolean process(TagNode node){
			return process(getTrackDetacher(), node);
		}

		/**
		 * @param detacher the track detacher
         * @param node the node that was repeated.
		 * @return true if you handled the retrack .
         */
		protected abstract boolean process(ITrackManager detacher, TagNode node);

	}

}
