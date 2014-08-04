/**
 * 
 */
package de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation.abstractdomain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretation.abstractdomain.booldomain.BoolValue;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.ProgramPoint;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RCFGEdge;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RCFGNode;

/**
 * An AbstractState represents an abstract program state, mapping variable
 * identifiers to abstract values. As variables can have different types,
 * for instance int or bool, different abstract domains may be used.
 * 
 * @author Christopher Dillo
 */
public class AbstractState {
	
	/**
	 * Stores information on a loop stack level, that is:
	 * - The loop entry node of the current loop,
	 * - The exit edge over which the loop will be left
	 * - Iteration counts for loops nested within the current loop
	 */
	public class LoopStackElement {
		private final ProgramPoint m_loopNode;
		private final RCFGEdge m_exitEdge;
		private final Map<ProgramPoint, Integer> m_iterationCounts = new HashMap<ProgramPoint, Integer>();
		public LoopStackElement(ProgramPoint loopNode, RCFGEdge exitEdge) {
			m_loopNode = loopNode;
			m_exitEdge = exitEdge;
		}
		public ProgramPoint getLoopNode() { return m_loopNode; }
		public RCFGEdge getExitEdge() { return m_exitEdge; }
		public int getIterationCount(ProgramPoint loopNode) { 
			Integer count = m_iterationCounts.get(loopNode);
			if (count == null) return 0;
			return count.intValue();
		}
		public void increaseIterationCount(ProgramPoint loopNode) {
			m_iterationCounts.put(loopNode, Integer.valueOf(getIterationCount(loopNode) + 1));
		}
		public LoopStackElement copy() {
			LoopStackElement result = new LoopStackElement(m_loopNode, m_exitEdge);
			for (ProgramPoint p : m_iterationCounts.keySet())
				result.m_iterationCounts.put(p, m_iterationCounts.get(p));
			return result;
		}
	}
	
	public class CallStackElement {
		private final String m_functionName;
		private final Map<String, IAbstractValue<?>> m_values;
		public CallStackElement(String functionName) {
			m_functionName = functionName;
			m_values = new HashMap<String, IAbstractValue<?>>();
		}
		public String getFunctionName() { return m_functionName; }
		public Map<String, IAbstractValue<?>> getValues() { return m_values; }
	}
	
	/**
	 * Stack of loop entry nodes along with the edge taken to enter the loop; used to make sure
	 * widening is applied properly in nested loops
	 */
	private final LinkedList<LoopStackElement> m_loopStack = new LinkedList<LoopStackElement>();
	
	/**
	 * Sequence of nodes passed leading to this state -> "execution trace" 
	 */
	private final List<RCFGNode> m_passedNodes = new LinkedList<RCFGNode>();
	
	/**
	 * True if the state at a node has been processed already (prevent processing the same state twice)
	 */
	private boolean m_isProcessed = false;
	
	private Logger m_logger;
	
	private IAbstractDomainFactory<?> m_numberFactory;
	private IAbstractDomainFactory<BoolValue.Bool> m_boolFactory;
	
	/**
	 * Stack of maps from variable identifiers to values. Stack levels represent scope levels,
	 * index 0 is the global scope.
	 */
	private final LinkedList<CallStackElement> m_callStack = new LinkedList<CallStackElement>();
	
	public AbstractState(Logger logger, IAbstractDomainFactory<?> numberFactory, IAbstractDomainFactory<BoolValue.Bool> boolFactory) {
		m_logger = logger;
		m_numberFactory = numberFactory;
		m_boolFactory = boolFactory;
		
		pushStackLayer(""); // global scope
		
		pushLoopEntry(null, null); // global iteration count
	}
	
	/**
	 * @return True iff this state contains all variables of the given state
	 * and all variable values are greater or equal to their corresponding values
	 */
	public boolean isSuper(AbstractState state) {
		// referring to this state as "greater"
		
		if (state == null)
			return false;
		
		List<CallStackElement> otherValues = state.getCallStack();
		
		// must have at least as many stack layers (scopes)
		if (m_callStack.size() < otherValues.size())
			return false;
		
		// for each stack layer (scope level) of the others (which may be less!)
		for (int i = 0; i < otherValues.size(); i++) {
			CallStackElement greaterLayer = m_callStack.get(i);
			CallStackElement smallerLayer = otherValues.get(i);
			
			// must be of the same function and have at least as many variables
			if ((!greaterLayer.getFunctionName().equals(smallerLayer.getFunctionName()))
					|| (greaterLayer.getValues().size() < smallerLayer.getValues().size()))
				return false;

			// check if any variable in the other state occurs and is greater in this state
			Set<String> smallerKeys = smallerLayer.getValues().keySet();
			for (String key : smallerKeys) {
				IAbstractValue<?> smallerValue = smallerLayer.getValues().get(key);
				IAbstractValue<?> greaterValue = greaterLayer.getValues().get(key); 
				
				// identifier must exist and thus have a value
				if (greaterValue == null)
					return false;
				
				// value of this state must be greater than the value of the other state
				if (!greaterValue.isSuper(smallerValue))
					return false;
			}
		}
		
		// all checks passed, this state is greater or equal to the argument state
		return true;
	}
	
	/**
	 * @return A copy of this abstract program state that is independent of this object.
	 */
	public AbstractState copy() {
		AbstractState result = new AbstractState(m_logger, m_numberFactory, m_boolFactory);
		
		result.m_callStack.clear();
		for (int i = 0; i < m_callStack.size(); i++) {
			CallStackElement cse = m_callStack.get(i);
			
			Map<String, IAbstractValue<?>> thisLayer = cse.getValues();
			
			result.m_callStack.add(new CallStackElement(cse.getFunctionName()));
			Map<String, IAbstractValue<?>> copyLayer = result.m_callStack.get(i).getValues();
			for (String identifier : thisLayer.keySet())
				copyLayer.put(identifier, thisLayer.get(identifier).copy());
		}
		
		result.m_loopStack.clear();
		result.m_loopStack.addAll(m_loopStack);
		
		result.m_passedNodes.addAll(m_passedNodes);
		
		return result;
	}
	
	/**
	 * Merge this state with the given state using the merge operator set in the preferences
	 * @param state The state to merge with
	 * @return A new merged state
	 */
	public AbstractState merge(AbstractState state) {		
		if (state == null)
			return null;
		
		IMergeOperator<?> mergeOp = m_numberFactory.getMergeOperator();
		IWideningOperator<BoolValue.Bool> boolMergeOp = m_boolFactory.getWideningOperator();
		
		List<CallStackElement> otherValues = state.getCallStack();

		AbstractState resultingState = new AbstractState(m_logger, m_numberFactory, m_boolFactory);
		List<CallStackElement> resultingValues = resultingState.getCallStack();
		
		int maxLayerCount = Math.max(m_callStack.size(), otherValues.size());
		
		// for each stack layer (scope level)
		resultingState.m_callStack.clear();
		for (int i = 0; i < maxLayerCount; i++) {
			CallStackElement thisCSE = (i < m_callStack.size()) ? m_callStack.get(i) : null;
			CallStackElement otherCSE = (i < otherValues.size()) ? otherValues.get(i) : null;
			Map<String, IAbstractValue<?>> thisLayer = (thisCSE != null) ? thisCSE.getValues() : null;
			Map<String, IAbstractValue<?>> otherLayer = (otherCSE != null) ? otherCSE.getValues() : null;
			
			resultingState.m_callStack.add(new CallStackElement((thisCSE == null ? otherCSE : thisCSE).getFunctionName()));
			Map<String, IAbstractValue<?>> resultingLayer = resultingValues.get(i).getValues();

			Set<String> identifiers = new HashSet<String>();
			if (thisLayer != null)
				identifiers.addAll(thisLayer.keySet());
			if (otherLayer != null)
				identifiers.addAll(otherLayer.keySet());

			// merge values (or take the single value if only one is present)
			for (String identifier : identifiers) {
				IAbstractValue<?> thisValue = (thisLayer == null) ? null : thisLayer.get(identifier);
				IAbstractValue<?> otherValue = (otherLayer == null) ? null : otherLayer.get(identifier); 

				IAbstractValue<?> resultingValue;
				if (thisValue == null) {
					resultingValue = otherValue.copy();
				} else if (otherValue == null) {
					resultingValue = thisValue.copy();
				} else {
					if (thisValue instanceof BoolValue)
						resultingValue = boolMergeOp.apply(thisValue, otherValue);
					else
						resultingValue = mergeOp.apply(thisValue, otherValue);
				}
				if (resultingValue != null)
					resultingLayer.put(identifier, resultingValue);
			}
		}
		
		// add passed loop entry nodes : take larger stack
		resultingState.m_loopStack.clear();
		resultingState.m_loopStack.addAll((m_loopStack.size() >= state.m_loopStack.size()) ? m_loopStack : state.m_loopStack);

		// add passed nodes of resultingState : take longer trace
		resultingState.m_passedNodes.addAll((m_passedNodes.size() >= state.m_passedNodes.size()) ? m_passedNodes : state.m_passedNodes);
		
		return resultingState;
	}
	
	/**
	 * Widen this state with the given state using the given widening operator set in the preferences
	 * @param state The state to widen with
	 * @return A new widened state: (this state) wideningOp (the given state)
	 */
	public AbstractState widen(AbstractState state) {
		if (state == null)
			return this.copy();
		
		IWideningOperator<?> wideningOp = m_numberFactory.getWideningOperator();
		IWideningOperator<BoolValue.Bool> boolWideningOp = m_boolFactory.getWideningOperator();
		
		List<CallStackElement> otherValues = state.getCallStack();

		AbstractState resultingState = new AbstractState(m_logger, m_numberFactory, m_boolFactory);
		List<CallStackElement> resultingValues = resultingState.getCallStack();
		
		int maxLayerCount = Math.max(m_callStack.size(), otherValues.size());
		
		// for each stack layer (scope level)
		resultingState.m_callStack.clear();
		for (int i = 0; i < maxLayerCount; i++) {
			CallStackElement thisCSE = (i < m_callStack.size()) ? m_callStack.get(i) : null;
			CallStackElement otherCSE = (i < otherValues.size()) ? otherValues.get(i) : null;
			Map<String, IAbstractValue<?>> thisLayer = (thisCSE != null) ? thisCSE.getValues() : null;
			Map<String, IAbstractValue<?>> otherLayer = (otherCSE != null) ? otherCSE.getValues() : null;

			resultingState.m_callStack.add(new CallStackElement((thisCSE == null ? otherCSE : thisCSE).getFunctionName()));
			Map<String, IAbstractValue<?>> resultingLayer = resultingValues.get(i).getValues();

			Set<String> identifiers = new HashSet<String>();
			if (thisLayer != null)
				identifiers.addAll(thisLayer.keySet());
			if (otherLayer != null)
				identifiers.addAll(otherLayer.keySet());

			// widen values: thisValue wideningOp otherValue
			for (String identifier : identifiers) {
				IAbstractValue<?> thisValue = (thisLayer == null) ? null : thisLayer.get(identifier);
				IAbstractValue<?> otherValue = (otherLayer == null) ? null : otherLayer.get(identifier); 

				IAbstractValue<?> resultingValue = null;
				if (thisValue == null) {
					resultingValue = otherValue.copy();
					m_logger.warn(String.format("Widening encountered a missing value for %s in the old state.", identifier));
				} else if (otherValue == null) {
					m_logger.error(String.format("Widening failed with a missing value for %s in the new state.", identifier));
				} else {
					if (thisValue instanceof BoolValue)
						resultingValue = boolWideningOp.apply(thisValue, otherValue);
					else
						resultingValue = wideningOp.apply(thisValue, otherValue);
				}
				if (resultingValue != null)
					resultingLayer.put(identifier, resultingValue);
			}
		}

		// add passed loop entry nodes : take stack from given (supposedly newer) state
		resultingState.m_loopStack.clear();
		for (LoopStackElement e : state.m_loopStack)
			resultingState.m_loopStack.add(e.copy());

		// add passed nodes of resultingState : take trace from given (supposedly newer) state
		for (RCFGNode node : state.m_passedNodes)
			resultingState.addPassedNode(node);
		
		return resultingState;
	}
	
	/**
	 * @param identifier
	 * @return The uppermost layer of the stack which contains a key for the given identifier
	 */
	private CallStackElement getTopmostLayerWithIdentifier(String identifier) {
		int layerNumber = 0;
		CallStackElement layerMap = null;
		
		boolean found = false;
		
		while (!found && (layerNumber < m_callStack.size())) {
			layerMap = m_callStack.get(layerNumber);
			found = layerMap.getValues().containsKey(identifier);
			layerNumber++;
		}
		
		return found ? layerMap : null;
	}

	/**
	 * @return The number of stack levels
	 */
	public int getStackSize() {
		return m_callStack.size();
	}
	
	/**
	 * @return The name of the current scope's function
	 */
	public String getCurrentScopeName() {
		return m_callStack.peek().getFunctionName();
	}
	
	/**
	 * Creates a new empty symbol table and puts it on the top of the stack
	 * @param functionName The name of the new scope's function name
	 */
	public void pushStackLayer(String functionName) {
		m_callStack.push(new CallStackElement(functionName));
	}
	
	/**
	 * Removes the topmost symbol table from the stack unless it is the only layer
	 * @return True if there was a table to pop, false if the stack only has a single layer
	 */
	public boolean popStackLayer() {
		int size = m_callStack.size();
		
		if (size <= 1)
			return false;
		
		m_callStack.pop();
		return true;
	}
	
	/**
	 * Assigns the given value to the topmost occurance of the identifier in the stack.
	 * @param identifier An existing identifier
	 * @param value The new value
	 * @return True iff a layer with the given identifier exists so the value could be written
	 */
	public boolean writeValue(String identifier, IAbstractValue<?> value) {
		CallStackElement layer = getTopmostLayerWithIdentifier(identifier);
		
		if (layer == null)
			return false;
		
		layer.getValues().put(identifier, value);
		
		return true;
	}
	
	/**
	 * @param identifier The identifier whose value is to be retrieved
	 * @return The value associated with the identifier on the topmost layer it occurs, or null if it is not found
	 */
	public IAbstractValue<?> readValue(String identifier) {
		CallStackElement layer = getTopmostLayerWithIdentifier(identifier);
		
		if (layer == null)
			return null;
		
		return layer.getValues().get(identifier);
	}
	
	/**
	 * Generates a new mapping for the given identifier on the topmost stack level if it does not exist there already.
	 * @param identifier The new identifier to declare
	 * @param initialValue Its initial value
	 * @return True if it could be declared, false if such an identifier already exists on the top layer or the stack is empty
	 */
	public boolean declareIdentifier(String identifier, IAbstractValue<?> initialValue) {
		CallStackElement topLayer = m_callStack.peek();

		m_logger.debug(String.format("New variable %s at scope level %d %s", identifier, getStackSize()-1, topLayer.getFunctionName()));
		
		if ((topLayer == null) || (topLayer.getValues().containsKey(identifier))) {
			m_logger.error("Cannot declare identifier!");
			return false;
		}
		
		topLayer.getValues().put(identifier, initialValue);
		
		return true;
	}
	
	/**
	 * Add a loop entry to the loop entry stack
	 * @param loopNode Loop entry node
	 * @param entryEdge The edge over which the loop will be left
	 */
	public void pushLoopEntry(ProgramPoint loopNode, RCFGEdge exitEdge) {
		m_loopStack.push(new LoopStackElement(loopNode, exitEdge));
	}

	/**
	 * Remove the top element of the loop entry stack
	 * @return The removed old top element of the loop entry stack
	 */
	public LoopStackElement popLoopEntry() {
		if (m_loopStack.size() <= 1) {
			m_logger.warn("Tried to pop the last, global loop stack level.");
			return null;
		} else {
			LoopStackElement lastLoop = m_loopStack.pop();
			if (lastLoop != null) {
				LoopStackElement currentLoop = m_loopStack.peek();
				currentLoop.increaseIterationCount(lastLoop.getLoopNode());
			}
			return lastLoop;
		}
	}
	
	/**
	 * @return The top element of the loop entry stack
	 */
	public LoopStackElement peekLoopEntry() {
		return m_loopStack.peek();
	}
	
	/**
	 * @return The stack of loop entry nodes along with the edges over which the loop has been entered
	 */
	public LinkedList<LoopStackElement> getLoopEntryNodes() {
		return m_loopStack;
	}
	
	/**
	 * Add a node to the list of passed nodes for trace reconstruction
	 * @param node A node to add
	 * @return True if the node was successfully added
	 */
	public boolean addPassedNode(RCFGNode node) {
		return m_passedNodes.add(node);
	}
	
	/**
	 * @return A list of nodes passed during creating this state
	 */
	public List<RCFGNode> getPassedNodes() {
		return m_passedNodes;
	}
	
	/**
	 * @param predecessor
	 * @return True iff this state's passed node list contains and begins with all passed nodes of the
	 * given state in the same order, plus at least one more node
	 */
	public boolean isSuccessor(AbstractState predecessor) {
		if (m_passedNodes.size() <= predecessor.m_passedNodes.size())
			return false;
		
		for (int i = 0; i < predecessor.m_passedNodes.size(); i++) {
			if (!m_passedNodes.get(i).equals(predecessor.m_passedNodes.get(i)))
				return false;
		}
		
		return true;
	}
	
	/**
	 * @return The stack as a list, bottom layer at index 0.
	 */
	public List<CallStackElement> getCallStack() {
		return m_callStack;
	}
	
	/**
	 * @return True if the state is set as having been processed already
	 */
	public boolean isProcessed() {
		return m_isProcessed;
	}
	
	/**
	 * @param processed Set that the state has been processed already
	 */
	public void setProcessed(boolean processed) {
		m_isProcessed = processed;
	}
}
