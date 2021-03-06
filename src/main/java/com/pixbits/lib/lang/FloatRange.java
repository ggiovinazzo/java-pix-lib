package com.pixbits.lib.lang;

import com.google.gson.*;
import com.pixbits.lib.json.JsonnableContext;

public class FloatRange implements JsonnableContext<FloatRange>
{
  public float min, max;
  
  public FloatRange() { }

  public FloatRange(String string)
  {
    String[] tks = string.split(":");
    
    try
    {
      if (tks.length != 2)
        throw new NumberFormatException();
      else
      {
        min = Float.valueOf(tks[0]);
        max = Float.valueOf(tks[1]);
      }
    }
    catch (NumberFormatException e)
    {
      min = 0.0f;
      max = 1.0f;
      throw e;
    }
  }
  
  public static FloatRange of(String string)
  {
     FloatRange range = new FloatRange(string);
     return range;
  }
  
  public static FloatRange ofJson(JsonElement element)
  {
    FloatRange range = new FloatRange();
    range.unserialize(element, null);
    return range;
  }
  
  public FloatRange(float min, float max) { this.min = min; this.max = max; }

  public String toString() { return min+":"+max; }
  
  @Override
  public JsonElement serialize(JsonSerializationContext context)
  {
    JsonArray a = new JsonArray();
    a.add(new JsonPrimitive(min));
    a.add(new JsonPrimitive(max));
    return a;
  }

  @Override
  public void unserialize(JsonElement element, JsonDeserializationContext context)
  {
    JsonArray a = element.getAsJsonArray();
    min = a.get(0).getAsFloat();
    max = a.get(1).getAsFloat();
  }
}