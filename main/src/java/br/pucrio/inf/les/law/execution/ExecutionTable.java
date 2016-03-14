package br.pucrio.inf.les.law.execution;

import br.pucrio.inf.les.law.model.Id;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class ExecutionTable {
    /**
     * Logger for this class
     */
    private static final Log LOG = LogFactory.getLog(ExecutionTable.class);

    private Hashtable<Id, ExecutionTableEntry> tableExecutionId = new Hashtable<Id, ExecutionTableEntry>();

    private Hashtable<Id, List<ExecutionTableEntry>> tableDefinitionId = new Hashtable<Id, List<ExecutionTableEntry>>();

    private List<IExecution> executionCache = new ArrayList<IExecution>();

    public synchronized void addEntry(Id executionId, Id definitionId, IExecution value,
            Context context) {
        ExecutionTableEntry entry = new ExecutionTableEntry(executionId,
                definitionId, value, context);
        // This adds a bit of redundance, however we gain performance when
        // searching either
        // by executionId or definitionId
        this.tableExecutionId.put(executionId, entry);

        List<ExecutionTableEntry> defEntry = this.tableDefinitionId
                .get(definitionId);
        if (defEntry == null) {
            defEntry = new Vector<ExecutionTableEntry>();
        }
        defEntry.add(entry);
        this.tableDefinitionId.put(definitionId, defEntry);

        // updates the cache
        executionCache.add(value);
    }

    public synchronized void removeExecution(IExecution execution) {
        tableExecutionId.remove(execution.getId());
        // updates the cache
        executionCache.remove(execution);

    }
    

    public IExecution getByExecutionId(Id executionId) {
        ExecutionTableEntry entry = this.tableExecutionId.get(executionId);
        if (entry == null) {
            LOG.warn("There is no execution instance with id=[" + executionId
                    + "]");
            return null;
        }
        return entry.getValue();
    }

    public List<IExecution> getByDefinitionIdAndContext(Id definitionId,
            Context context) {
        List<ExecutionTableEntry> entries = this.tableDefinitionId
                .get(definitionId);
        if (entries == null) {
            LOG.debug("There is no execution instance with definitionId=["
                    + definitionId + "]");
            return null;
        }
        Vector<IExecution> result = new Vector<IExecution>();
        for (ExecutionTableEntry entry : entries) {
            if (entry.hasEqualsContext(context)) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    public List<IExecution> getByDefinitionId(Id descriptorId) {
        List<IExecution> result = new LinkedList<IExecution>();
        List<ExecutionTableEntry> entries = this.tableDefinitionId
                .get(descriptorId);
        if (entries != null) {
            for (ExecutionTableEntry entry : entries) {
                result.add(entry.getValue());
            }
        }
        return result;
    }

    public List<IExecution> getAllExecutions() {
        return executionCache;
    }

	

}
