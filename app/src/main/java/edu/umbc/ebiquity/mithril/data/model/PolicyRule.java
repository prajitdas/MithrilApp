package edu.umbc.ebiquity.mithril.data.model;

import edu.umbc.ebiquity.mithril.data.model.rules.actions.RuleAction;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;

public class PolicyRule {
    private int id;
    private String name;
    private int ctxId;
    private int appId;
    private RuleAction action;
    private SemanticUserContext semanticUserContext;

    public PolicyRule(int id, String name, int ctxId, int appId, RuleAction action, SemanticUserContext semanticUserContext) {
        this.id = id;
        this.name = name;
        this.ctxId = ctxId;
        this.appId = appId;
        this.action = action;
        this.semanticUserContext = semanticUserContext;
    }

    public PolicyRule() {
        super();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCtxId() {
        return ctxId;
    }

    public void setCtxId(int ctxId) {
        this.ctxId = ctxId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public RuleAction getAction() {
        return action;
    }

    public void setAction(RuleAction action) {
        this.action = action;
    }

    public SemanticUserContext getSemanticUserContext() {
        return semanticUserContext;
    }

    public void setSemanticUserContext(SemanticUserContext semanticUserContext) {
        this.semanticUserContext = semanticUserContext;
    }
}