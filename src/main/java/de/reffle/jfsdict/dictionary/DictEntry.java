package de.reffle.jfsdict.dictionary;


public class DictEntry {
  private String key;
  private int    value;

  public DictEntry() {
  }

  public DictEntry(String aKey, int aValue) {
    setKey(aKey);
    setValue(aValue);
  }

  public String getKey() {
    return key;
  }

  public void setKey(String aKey) {
    key = aKey;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int aValue) {
    value = aValue;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("(")
      .append(key)
      .append(",")
      .append(value)
      .append(")");
    return sb.toString();

  }
}
