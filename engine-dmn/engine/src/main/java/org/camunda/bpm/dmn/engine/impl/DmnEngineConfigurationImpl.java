/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.camunda.bpm.dmn.engine.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.DmnEngineConfiguration;
import org.camunda.bpm.dmn.engine.ScriptEngineResolver;
import org.camunda.bpm.dmn.engine.context.DmnContextFactory;
import org.camunda.bpm.dmn.engine.handler.DmnElementHandlerRegistry;
import org.camunda.bpm.dmn.engine.hitpolicy.DmnHitPolicyHandler;
import org.camunda.bpm.dmn.engine.impl.context.DmnContextFactoryImpl;
import org.camunda.bpm.dmn.engine.impl.handler.DmnElementHandlerRegistryImpl;
import org.camunda.bpm.dmn.engine.impl.hitpolicy.AnyHitPolicyHandler;
import org.camunda.bpm.dmn.engine.impl.hitpolicy.CollectHitPolicyHandler;
import org.camunda.bpm.dmn.engine.impl.hitpolicy.FirstHitPolicyHandler;
import org.camunda.bpm.dmn.engine.impl.hitpolicy.OutputOrderHitPolicyHandler;
import org.camunda.bpm.dmn.engine.impl.hitpolicy.PriorityHitPolicyHandler;
import org.camunda.bpm.dmn.engine.impl.hitpolicy.RuleOrderHitPolicyHandler;
import org.camunda.bpm.dmn.engine.impl.hitpolicy.UniqueHitPolicyHandler;
import org.camunda.bpm.dmn.engine.impl.transform.DmnTransformFactoryImpl;
import org.camunda.bpm.dmn.engine.impl.transform.DmnTransformerImpl;
import org.camunda.bpm.dmn.engine.transform.DmnTransformFactory;
import org.camunda.bpm.dmn.engine.transform.DmnTransformListener;
import org.camunda.bpm.dmn.engine.transform.DmnTransformer;
import org.camunda.bpm.model.dmn.HitPolicy;

public class DmnEngineConfigurationImpl implements DmnEngineConfiguration {

  protected DmnContextFactory contextFactory;

  protected DmnTransformer transformer;
  protected DmnTransformFactory transformFactory;
  protected DmnElementHandlerRegistry elementHandlerRegistry;
  protected List<DmnTransformListener> customPreDmnTransformListeners = new ArrayList<DmnTransformListener>();
  protected List<DmnTransformListener> customPostDmnTransformListeners = new ArrayList<DmnTransformListener>();
  protected Map<HitPolicy, DmnHitPolicyHandler> hitPolicyHandlers;
  protected ScriptEngineResolver scriptEngineResolver;

  public DmnContextFactory getDmnContextFactory() {
    return contextFactory;
  }

  public void setDmnContextFactory(DmnContextFactory contextFactory) {
    this.contextFactory = contextFactory;
  }

  public DmnTransformer getTransformer() {
    return transformer;
  }

  public DmnTransformFactory getTransformFactory() {
    return transformFactory;
  }

  public void setTransformFactory(DmnTransformFactory transformFactory) {
    this.transformFactory = transformFactory;
  }

  public DmnElementHandlerRegistry getElementHandlerRegistry() {
    return elementHandlerRegistry;
  }

  public void setElementHandlerRegistry(DmnElementHandlerRegistry elementHandlerRegistry) {
    this.elementHandlerRegistry = elementHandlerRegistry;
  }

  public List<DmnTransformListener> getCustomPreDmnTransformListeners() {
    return customPreDmnTransformListeners;
  }

  public void setCustomPreDmnTransformListeners(List<DmnTransformListener> customPreDmnTransformListeners) {
    this.customPreDmnTransformListeners = customPreDmnTransformListeners;
  }

  public List<DmnTransformListener> getCustomPostDmnTransformListeners() {
    return customPostDmnTransformListeners;
  }

  public void setCustomPostDmnTransformListeners(List<DmnTransformListener> customPostDmnTransformListeners) {
    this.customPostDmnTransformListeners = customPostDmnTransformListeners;
  }

  public Map<HitPolicy, DmnHitPolicyHandler> getHitPolicyHandlers() {
    return hitPolicyHandlers;
  }

  public void setHitPolicyHandlers(Map<HitPolicy, DmnHitPolicyHandler> hitPolicyHandlers) {
    this.hitPolicyHandlers = hitPolicyHandlers;
  }

  public ScriptEngineResolver getScriptEngineResolver() {
    return scriptEngineResolver;
  }

  public void setScriptEngineResolver(ScriptEngineResolver scriptEngineResolver) {
    this.scriptEngineResolver = scriptEngineResolver;
  }

  public DmnEngine buildEngine() {
    init();
    return new DmnEngineImpl(this);
  }

  protected void init() {
    initContextFactory();
    initTransformFactory();
    initElementHandlerRegistry();
    initTransformer();
    initHitPolicyHandlers();
    initScriptEngineResolver();
  }

  protected void initContextFactory() {
    if (contextFactory == null) {
      contextFactory = new DmnContextFactoryImpl();
    }
  }

  public void initTransformFactory() {
    if (transformFactory == null) {
      transformFactory = new DmnTransformFactoryImpl();
    }
  }

  protected void initElementHandlerRegistry() {
    if (elementHandlerRegistry == null) {
      elementHandlerRegistry = new DmnElementHandlerRegistryImpl();
    }
  }

  protected void initTransformer() {
    transformer = new DmnTransformerImpl(transformFactory, elementHandlerRegistry);
    if (customPreDmnTransformListeners != null) {
      transformer.getTransformListeners().addAll(customPreDmnTransformListeners);
    }
    transformer.getTransformListeners().addAll(getDefaultDmnTransformListeners());
    if (customPostDmnTransformListeners != null) {
      transformer.getTransformListeners().addAll(customPostDmnTransformListeners);
    }
  }

  protected List<DmnTransformListener> getDefaultDmnTransformListeners() {
    return Collections.emptyList();
  }

  protected void initHitPolicyHandlers() {
    if (hitPolicyHandlers == null) {
      hitPolicyHandlers = getDefaultHitPolicyHandlers();
    }
  }

  protected Map<HitPolicy, DmnHitPolicyHandler> getDefaultHitPolicyHandlers() {
    Map<HitPolicy, DmnHitPolicyHandler> hitPolicyHandlers = new HashMap<HitPolicy, DmnHitPolicyHandler>();
    hitPolicyHandlers.put(HitPolicy.UNIQUE, new UniqueHitPolicyHandler());
    hitPolicyHandlers.put(HitPolicy.ANY, new AnyHitPolicyHandler());
    hitPolicyHandlers.put(HitPolicy.PRIORITY, new PriorityHitPolicyHandler());
    hitPolicyHandlers.put(HitPolicy.FIRST, new FirstHitPolicyHandler());
    hitPolicyHandlers.put(HitPolicy.OUTPUT_ORDER, new OutputOrderHitPolicyHandler());
    hitPolicyHandlers.put(HitPolicy.RULE_ORDER, new RuleOrderHitPolicyHandler());
    hitPolicyHandlers.put(HitPolicy.COLLECT, new CollectHitPolicyHandler());
    return hitPolicyHandlers;
  }

  protected void initScriptEngineResolver() {
    if (scriptEngineResolver == null) {
      scriptEngineResolver = new DefaultScriptEngineResolver();
    }
  }

}
