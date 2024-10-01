package commands;

import java.io.Serializable;
import java.util.HashMap;

public class CommandMap extends HashMap<String, Class<? extends Command>> implements Serializable {
    public Class<? extends Command> get(String name){
        if(!this.keySet().contains(name))
            return BlankCommand.class;
        return super.get(name);
    }
}
