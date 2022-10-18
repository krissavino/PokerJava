package Commands.Model;

import com.google.gson.Gson;

import java.util.ArrayList;

public class CommandModel
{
    ArrayList<Object> objects;
    public String operationName = "None";

    public void set(String operationName, Object ... objects) {
        this.objects = new ArrayList<Object>();
        this.operationName = operationName;
        this.objects.add(operationName);
        for (Object obj : objects) {
            this.objects.add(obj);
        }
    }

    public String getString() {
        Gson gson = new Gson();
        String[] info = new String[objects.size()];
        for(int i = 0; i < info.length; i++) {
            info[i] = gson.toJson(objects.get(i));
        }
        String str = gson.toJson(info);

        return str;
    }
}


