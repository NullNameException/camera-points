package com.camerapoints.utility;

public enum Direction
{
    NONE(0, "Unchanged"),
    NORTH(1, "North"),
    EAST(2, "East"),
    SOUTH(3, "South"),
    WEST(4, "West");

    private final int value;
    private final String name;

    Direction(int value, String name)
    {
        this.value = value;
        this.name = name;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return name;
    }
}