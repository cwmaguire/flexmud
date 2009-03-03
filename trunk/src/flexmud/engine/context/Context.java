/*
Copyright 2009 Chris Maguire (cwmaguire@gmail.com)

flexmud is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

flexmud is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with flexmud.  If not, see <http://www.gnu.org/licenses/>.
 */
package flexmud.engine.context;

import flexmud.engine.cmd.AliasCommandClassNameMap;
import flexmud.engine.cmd.Command;
import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "context")
public class Context {
    private static final Logger LOGGER = Logger.getLogger(Context.class);
    public static final String ID_PROPERTY = "id";
    public static final String NAME_PROPERTY = "name";
    public static final String CHILD_GROUP_PROPERTY = "childGroup";
    public static final String PARENT_GROUP_PROPERTY = "parentGroup";
    public static final String COMMAND_CLASS_NAME_ALIASES_PROPERTY = "aliasCommandClassNameMaps";
    public static final String ENTRY_MESSAGE_PROPERTY = "entryMessage";
    public static final String IS_LISTED_IN_PARENT_PROPERTY = "isListedInParent";
    public static final String DOES_USE_CHARACTER_PROMPT_PROPERTY = "isCharacterPromptable";
    public static final String MAX_ENTRIES_PROPERTY = "maxEntries";
    public static final String MAX_ENTRIES_EXCEEDED_MESSAGE_PROPERTY = "maxEntriesExceededMessage";
    public static final String ENTRY_COMMAND_CLASS_NAME_PROPERTY = "entryCommandClassName";
    public static final String PROMPT_COMMAND_CLASS_NAME_PROPERTY = "promptCommandClassName";

    private long id;
    private String name;
    private ContextGroup childGroup;
    private ContextGroup parentGroup;
    private Set<AliasCommandClassNameMap> aliasCommandClassNameMaps = new HashSet<AliasCommandClassNameMap>();
    private String entryMessage;
    private boolean isListedInParent;
    private boolean isCharacterPromptable;
    private int maxEntries = -1;
    private String maxEntriesExceededMessage = "";
    private String entryCommandClassName;
    private String promptCommandClassName;

    private Class entryCommandClass;
    private Class promptCommandClass;
    private Map<String, Class> aliasCommandClasses = new HashMap<String, Class>();

    public Context() {
    }

    public Context(String name){
        this();
        this.name = name;
    }

    public void init(){

        entryCommandClass = loadClass(entryCommandClassName);
        promptCommandClass = loadClass(promptCommandClassName);

        mapAliasesToCommandClasses();
    }

    private void mapAliasesToCommandClasses() {
        if(aliasCommandClassNameMaps != null){
            for(AliasCommandClassNameMap aliasMap : aliasCommandClassNameMaps){
                mapCommandAliases(loadClass(aliasMap.getCommandClassName()), aliasMap.getAliases());
            }
        }
    }

    private void mapCommandAliases(Class commandClass, Set<String> aliases) {
        if(commandClass != null){
            for(String alias : aliases){
                aliasCommandClasses.put(alias, commandClass);
            }
        }
    }

    private Class loadClass(String className) {
        Class classFromName = null;
        if(className != null){
            try{
                classFromName = Class.forName(className);
            }catch(Exception e){
                LOGGER.error("Could not load command " + className + " for context " + name, e);
                return null;
            }
        }
        return classFromName;
    }


    @Id
    @GeneratedValue
    @Column(name = "context_id")
    public long getId() {
        return id;
    }

    private void setId(long id){
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "entry_message")
    public String getEntryMessage() {
        return entryMessage;
    }

    public void setEntryMessage(String entryMessage) {
        this.entryMessage = entryMessage;
    }

    @Column(name = "is_listed_in_parent")
    public boolean isListedInParent() {
        return isListedInParent;
    }

    public void setListedInParent(boolean listedInParent) {
        isListedInParent = listedInParent;
    }

    @Column(name = "is_character_promptable")
    public boolean isCharacterPromptable() {
        return isCharacterPromptable;
    }

    public void setCharacterPromptable(boolean characterPromptable) {
        this.isCharacterPromptable = characterPromptable;
    }

    @Column(name = "entry_command_class_name")
    public String getEntryCommandClassName() {
        return entryCommandClassName;
    }

    public void setEntryCommandClassName(String entryCommandClassName) {
        this.entryCommandClassName = entryCommandClassName;
    }

    @Column(name = "prompt_command_class_name")
    public String getPromptCommandClassName() {
        return promptCommandClassName;
    }

    public void setPromptCommandClassName(String promptCommandClassName) {
        this.promptCommandClassName = promptCommandClassName;
    }

    @Column(name = "max_entries")
    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int maxEntries) {
        this.maxEntries = maxEntries;
    }

    @Column(name = "max_entries_exceeded_message")
    public String getMaxEntriesExceededMessage() {
        return maxEntriesExceededMessage;
    }

    public void setMaxEntriesExceededMessage(String maxEntriesExceededMessage) {
        this.maxEntriesExceededMessage = maxEntriesExceededMessage;
    }

    @OneToOne(targetEntity = ContextGroup.class, cascade = CascadeType.ALL, optional = true)
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinColumn(name = "child_group_id")
    public ContextGroup getChildGroup(){
        return childGroup;
    }

    public void setChildGroup(ContextGroup childGroup) {
        this.childGroup = childGroup;
    }

    @ManyToOne
    @JoinColumn(name = "parent_group_id", nullable = true)
    public ContextGroup getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(ContextGroup parentGroup) {
        this.parentGroup = parentGroup;
    }


    @OneToMany(mappedBy = AliasCommandClassNameMap.CONTEXT_PROPERTY, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    public Set<AliasCommandClassNameMap> getAliasCommandClassNameMaps() {
        return aliasCommandClassNameMaps;
    }

    public void setAliasCommandClassNameMaps(Set<AliasCommandClassNameMap> aliasCommandClassNameMaps) {
        this.aliasCommandClassNameMaps = aliasCommandClassNameMaps;
    }

    @Transient
    public Map<String, Class> getAliasCommandClasses() {
        return aliasCommandClasses;
    }

    @Transient
    public Command getEntryCommand() {
        try{
            return (Command) entryCommandClass.newInstance();
        }catch(Exception e){
            LOGGER.error("Could not instantiate instance of entry command " + entryCommandClass.getName() + " for context " + name, e);
        }

        return null;
    }

    @Transient
    public Command getPromptCommand() {
        try {
            return (Command) promptCommandClass.newInstance();
        } catch (Exception e) {
            LOGGER.error("Could not instantiate instance of entry command " + entryCommandClass.getName() + " for context " + name, e);
        }

        return null;
    }

    @Transient
    public Class getCommandForAlias(String alias){
        return aliasCommandClasses.get(alias);
    }
}
