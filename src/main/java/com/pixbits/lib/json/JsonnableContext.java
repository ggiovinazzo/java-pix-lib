package com.pixbits.lib.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;

public interface JsonnableContext<T>
{
  JsonElement serialize(JsonSerializationContext context) throws IllegalAccessException;
  void unserialize(JsonElement element, JsonDeserializationContext context) throws IllegalAccessException;
}
