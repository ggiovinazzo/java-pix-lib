package com.pixbits.lib.yaml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pixbits.lib.yaml.unserializer.EnumUnserializer;
import com.pixbits.lib.yaml.unserializer.ListUnserializer;
import com.pixbits.lib.yaml.unserializer.ReflectiveUnserializer;

public class YamlEnvironment
{
  private final Map<Class<?>, YamlUnserializer<?>> unserializers;

  private void setupBasicUnserializers()
  {
    unserializers.put(Integer.class, n -> {
      try
      {
        int value = Integer.parseInt(n.rawGet());
        return value;
      }
      catch (NumberFormatException e)
      {
        throw new YamlException("value is not convertible to int", e);
      }
    });
    
    unserializers.put(Integer.TYPE, unserializers.get(Integer.class));
    
    unserializers.put(String.class, n -> {
      try
      {
        return n.rawGet();
      }
      catch (ClassCastException e)
      {
        throw new YamlException("value is not convertible to String", e);
      }
    });
    
    
  }
  
  public YamlEnvironment(YamlParser parser)
  {
    unserializers = new HashMap<>();
    setupBasicUnserializers();
  }
  
  public <T> void registerUnserializer(Class<T> type, YamlUnserializer<T> unserializer)
  {
    unserializers.put(type, unserializer);
  }
  
  @SuppressWarnings("unchecked")
  public <T> YamlUnserializer<T> findUnserializer(Class<T> type)
  {
    YamlUnserializer<T> unserializer = (YamlUnserializer<T>)unserializers.get(type);
    
    if (unserializer != null)
      return unserializer;
    else if (type.isEnum())
    {
      return (YamlUnserializer<T>)unserializers
          .computeIfAbsent(type, t -> new EnumUnserializer<>(t));
    }
    else if (List.class.equals(type))
    {
      return (YamlUnserializer<T>)unserializers
          .computeIfAbsent(type, t -> new ListUnserializer<>(t));
    }
    else
    {
      return (YamlUnserializer<T>)unserializers
          .computeIfAbsent(type, t -> new ReflectiveUnserializer<>(t));
    }
  }
}