package com.smp.obdscanner.displaydata;

/**
 * Created by Steve on 8/6/14.
 */
public enum InformationType
{
    PERFORMANCE(0), DASH(1), DATA(2), FAULTS(3), ADAPTER(4);

    private int value;

    private InformationType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;

    }
    /*
    @Override
    public String toString()
    {
        switch (this)
        {
            case PERFORMANCE:
                return "Performance";
            case DASH:
                return "Dash";
            case DATA:
                return "Data";
            case FAULTS:
                return "Faults";
            case ADAPTER:
                return "Adapter";
            default:
                throw new UnsupportedOperationException();

        }
    }
    */
}
