package org.pharmgkb.parser.vcf.model;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link VcfSample}.
 * @author Douglas Myers-Turnbull
 */
public class VcfSampleTest {

  @Test
  public void testGetProperty() {
    List<String> keys = Arrays.asList("key1", "key2");
    List<String> values = Arrays.asList("value1", "value2");
    VcfSample sample = new VcfSample(keys, values);
    assertEquals(2, sample.getPropertyKeys().size());
    assertEquals("value1", sample.getProperty("key1"));
  }

  @Test
  public void testOrder() {
    LinkedHashMap<String, String> map = new LinkedHashMap<>();
    map.put("1", "A");
    map.put("2", "B");
    VcfSample sample = new VcfSample(map);
    assertEquals(Arrays.asList("1", "2"), new ArrayList<>(sample.getPropertyKeys()));

    LinkedHashMap<String, String> reversedMap = new LinkedHashMap<>();
    reversedMap.put("2", "B");
    reversedMap.put("1", "A");
    VcfSample sampleReverse = new VcfSample(reversedMap);
    assertEquals(Arrays.asList("2", "1"), new ArrayList<>(sampleReverse.getPropertyKeys()));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBadConstructor() {
    List<String> keys = Arrays.asList("key1", "key2");
    List<String> values = Arrays.asList("value1");
    new VcfSample(keys, values);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testHasNewline() {
    List<String> keys = Arrays.asList("key1");
    List<String> values = Arrays.asList("value\n1");
    new VcfSample(keys, values);
  }

}