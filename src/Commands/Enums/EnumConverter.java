package Commands.Enums;

public class EnumConverter
{
    public static CommandEnum GetCommandEnum(String string)
    {
        for(CommandEnum commandEnum : CommandEnum.values())
            if(string.equals(commandEnum.toString()) == true)
                return commandEnum;
        return CommandEnum.Empty;
    }
}
