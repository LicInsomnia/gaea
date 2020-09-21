package com.tincery.gaea.core.base.rule;


import com.tincery.gaea.api.src.AbstractSrcData;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 规则注册中心
 * 此类是单例
 * 此类存放着所有注册过的规则
 * @author gxz
 *
 */
public class RuleRegistry implements Serializable {
	private static final long serialVersionUID = 1L;


	private volatile List<Rule> ruleBox;

	/** 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载 */
	private static class SingletonHolder {
		/** 静态初始化器，由JVM来保证线程安全 */
		private static final RuleRegistry INSTANCE = new RuleRegistry();
	}

	/**
	 * 获得单例的 {@link RuleRegistry}
	 *
	 * @return {@link RuleRegistry}
	 */
	public static RuleRegistry getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private RuleRegistry() {

	}



	/****
	 * 注册规则
	 * @author gxz
	 * @param rule 规则
	 * @return 注册器
	 **/
	public RuleRegistry putRule(Rule rule){
		if(null == ruleBox){
			synchronized (this){
				if(null == ruleBox){
					ruleBox = new CopyOnWriteArrayList<>();
				}
			}
		}
		ruleBox.add(rule);
		return this;
	}

	/****
	 * 获得规则集合
	 * @return 规则
	 **/
	public List<Rule> getRules(){
		return this.ruleBox;
	}


	/***
	 * 循环匹配 直到找到一个可以匹配的规则 或者在实现类中手动终止了匹配
	 * @see Rule#matchOrStop(AbstractSrcData)
	 * @author gxz
	 * @param model  一个实体
	 * @return boolean 是否匹配到
	 **/
	public  <T extends AbstractSrcData> boolean matchLoop(T model){
		if(CollectionUtils.isEmpty(ruleBox)){
			return false;
		}
		for (Rule box : ruleBox) {
			if(box.isActivity() && box.matchOrStop(model)){
				return true;
			}
		}
		return false;
	}

}
