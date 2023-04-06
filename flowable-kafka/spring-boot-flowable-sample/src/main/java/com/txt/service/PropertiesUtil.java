package com.txt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class PropertiesUtil {


    private static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        return String.valueOf(obj);
    }

    private static String toEmpty(Object obj) {
        if (obj == null) {
            return "";
        }
        return String.valueOf(obj);
    }

    public static String getStringValue(Object object, String fieldName) {
        try {
            if (object != null) {
                Class<?> clazz = object.getClass();
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return toString(field.get(object));
            }
        } catch (Exception e) {
//            "Cannot access property " + fieldName + " on class:" + object.getClass().getName());
            e.printStackTrace();
        }
        return null;
    }

    public static Object getProperties(Object object, String fieldName) {
        try {
            if (object != null) {
                Class<?> clazz = object.getClass();
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(object);
            }
        } catch (Exception e) {
//            "Cannot access property " + fieldName + " on class:" + object.getClass().getName());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setProperties(Object object, String fieldName, Object fieldValue) {
        try {
            if (object != null) {
                Class<?> clazz = object.getClass();
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, deepClone(fieldValue));
            }
        } catch (Exception e) {
//            "Cannot access property " + fieldName + " on class:" + object.getClass().getName());
            e.printStackTrace();
            return true;
        }
        return true;
    }

    public static boolean copyProperties(Object objTo, Object objFr) {
        List<String> feildNameTo = listAllFields(objTo);
        try {
            for (Field fieldFr : objFr.getClass().getDeclaredFields()) {
                fieldFr.setAccessible(true);
                if (Modifier.isFinal(fieldFr.getModifiers())) {
                    continue;
                }
                Class<?> clazz = objTo.getClass();
                Field fieldTo = null;
                if (!feildNameTo.contains(fieldFr.getName())) {
                    continue;
                }

                try {
                    fieldTo = clazz.getDeclaredField(fieldFr.getName());
                } catch (SecurityException e) {
                    throw new RuntimeException(
                            "Cannot get property " + fieldFr.getName() + " on class:" + clazz.getName());
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException("No Such Field " + fieldFr.getName() + " on class:" + clazz.getName());
                }

                fieldTo.setAccessible(true);
                if (Modifier.isFinal(fieldTo.getModifiers())) {
                    continue;
                }

                if (!fieldTo.getType().equals(fieldFr.getType())) {
                    continue;
                }

                try {
                    fieldTo.set(objTo, deepClone(fieldFr.get(objFr)));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(
                            "IllegalArgument " + fieldTo.getName() + " on class:" + clazz.getName() + " invalid");
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("cannot access " + fieldTo.getName() + " on class:" + clazz.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean copySameProperties(Object objTo, Object objFrom) {
        try {
            if (objTo != null && objFrom != null) {

                List<String> feildNameTo = listAllFields(objTo);
                for (Field feildFrom : objFrom.getClass().getDeclaredFields()) {
                    feildFrom.setAccessible(true);
                    if (Modifier.isFinal(feildFrom.getModifiers()) || !feildNameTo.contains(feildFrom.getName())) {
                        continue;
                    }
                    Field fieldTo = null;
                    Class<?> clazz = objTo.getClass();
                    try {
                        fieldTo = clazz.getDeclaredField(feildFrom.getName());
                    } catch (SecurityException e) {
                        throw new RuntimeException(
                                "Can't access Field " + feildFrom.getName() + " class " + clazz.getName());
                    } catch (NoSuchFieldException e) {
                        throw new RuntimeException(
                                "No Such Field " + feildFrom.getName() + " class " + clazz.getName());
                    }

                    fieldTo.setAccessible(true);
                    if (Modifier.isFinal(fieldTo.getModifiers()) || !fieldTo.getType().equals(feildFrom.getType())) {
                        continue;
                    }
                    try {
                        fieldTo.set(objTo, feildFrom.get(objFrom));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException(
                                "Illegal Argument " + fieldTo.getName() + " class:" + clazz.getName() + " invalid");
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Illegal Access " + fieldTo.getName() + " class:" + clazz.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<String> listAllFields(Object obj) {
        List<String> lst = new ArrayList<String>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            lst.add(field.getName());
        }
        return lst;
    }

    public static void main(String[] args) throws JsonProcessingException {
        EventDto eventDto = new EventDto();
        eventDto.setVar01("222");
        eventDto.setVar02(true);
        eventDto.setVar03(888);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.convertValue(eventDto, JsonNode.class);
        Object obj = jsonNode;

        listAllFields(obj).forEach(s -> {
//            setProperties(eventDto, "var01", "sjfsfhgshfgsfsf");
//            Object p = getProperties(eventDto, s);

            System.out.println(s);
        });
    }
}
