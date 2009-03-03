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

import org.apache.log4j.Logger;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "context")
public class Context {
    private static final Logger LOGGER = Logger.getLogger(Context.class);
    public static String ID_PROPERTY = "id";
    public static String NAME_PROPERTY = "name";
    public static String CHILD_GROUP_PROPERTY = "childGroup";
    public static String PARENT_GROUP_PROPERTY = "parentGroup";
    public static String COMMAND_CLASS_NAME_ALIASES_PROPERTY = "commandClassNameAliases";
    public static String ENTRY_MESSAGE_PROPERTY = "entryMessage";
    public static String IS_LISTED_IN_PARENT_PROPERTY = "isListedInParent";
    public static String DOES_USE_CHARACTER_PROMPT_PROPERTY = "isCharacterPromptable";
    public static String MAX_ENTRIES_PROPERTY = "maxEntries";
    public static String MAX_ENTRIES_EXCEEDED_MESSAGE_PROPERTY = "maxEntriesExceededMessage";
    public static String ENTRY_COMMAND_CLASS_NAME_PROPERTY = "entryCommandClassName";

    private long id;
    private String name;
    private ContextGroup childGroup;
    private ContextGroup parentGroup;
    private Map<String, String> commandClassNameAliases = new HashMap<String, String>();
    private String entryMessage;
    private boolean isListedInParent;
    private boolean isCharacterPromptable;
    private int maxEntries = -1;
    private String maxEntriesExceededMessage = "";
    private String entryCommandClassName;

    private Class entryCommandClass;
    private Map<Class, String> commandClassAliases = new HashMap<Class, String>();

    public Context(){
    }

    public Context(String name){
        this();
        this.name = name;
    }

    public void init(){
        if(entryCommandClass != null){
            try{
                entryCommandClass = Class.forName(entryCommandClassName);
            }catch(Exception e){
                LOGGER.error("Could not load entry command for context " + name, e);
            }
        }

        if(commandClassNameAliases != null){
            for(String className : commandClassNameAliases.keySet()){
                try {
                    commandClassAliases.put(Class.forName(className), commandClassNameAliases.get(className));
                } catch (Exception e) {
                    LOGGER.error("Could not commands and aliases for context " + name, e);
                }
            }
        }
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


    @CollectionOfElements( targetElement = String.class )
    @JoinTable(name = "context_command", joinColumns = @JoinColumn(name = "context_id"))
    public Map<String, String> getCommandClassNameAliases() {
        return commandClassNameAliases;
    }

    public void setCommandClassNameAliases(Map<String, String> commandClassNameAliases) {
        this.commandClassNameAliases = commandClassNameAliases;
    }

    @Transient
    public Map<Class, String> getCommandClassAliases() {
        return commandClassAliases;
    }

    public void setCommandClassAliases(Map<Class, String> commandClassAliases) {
        this.commandClassAliases = commandClassAliases;
    }

    @Transient
    public Runnable getEntryCommand() {
        try{
            return (Runnable) entryCommandClass.newInstance();
        }catch(Exception e){
            LOGGER.error("Could not instantiate instance of entry command " + entryCommandClass.getName() + " for context " + name, e);
        }

        return null;
    }
}
