package org.pharmgkb.parser.vcf.model;

import com.google.common.base.Joiner;
import com.google.common.collect.ListMultimap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


/**
 * This class contains the basic data (the first 9 columns) for a VCF position line.
 * <pre>
 * 0 - CHROM
 * 1 - POS
 * 2 - ID
 * 3 - REF
 * 4 - ALT
 * 5 - QUAL
 * 6 - FILTER
 * 7 - INFO
 * 8 - FORMAT
 * </pre>
 *
 * @author Mark Woon
 */
public class VcfPosition {
  private static final Pattern sf_qualPattern = Pattern.compile("([\\d\\.]+|\\.)");
  private static final Joiner sf_commaJoiner = Joiner.on(",");
  private String m_chromosome;
  private long m_position;
  private List<String> m_ids;
  private List<String> m_refBases;
  private List<String> m_altBases;
  private List<String> m_alleles = new ArrayList<>();
  private String m_quality;
  private String m_filter;
  private ListMultimap<String, String> m_info;
  private List<String> m_format;


  public VcfPosition(@Nonnull String chr, long pos,
      @Nullable List<String> ids,
      @Nonnull List<String> refBases,
      @Nullable List<String> altBases,
      @Nonnull String qual,
      @Nonnull String filter,
      @Nullable ListMultimap<String, String> info,
      @Nullable List<String> format) {

    // not resolving ID string
    m_chromosome = chr;
    m_position = pos;
    if (ids == null) {
      m_ids = Collections.emptyList();
    } else {
      m_ids = ids;
    }
    m_refBases = refBases;
    m_alleles.addAll(m_refBases);
    if (altBases == null) {
      m_altBases = Collections.emptyList();
    } else {
      m_altBases = altBases;
      m_alleles.addAll(altBases);
    }

    if (!sf_qualPattern.matcher(qual).matches()) {
      throw new IllegalArgumentException("[QUAL] Not contain a number: '" + qual + "'");
    }
    m_quality = qual;

    m_filter = filter;

    m_info = info;

    if (format == null) {
      m_format = Collections.emptyList();
    } else {
      m_format = format;
    }
  }


  /**
   * Gets an identifier from the reference genome or an angle-bracketed ID String ("<ID>") pointing to a contig in the
   * assembly file.
   */
  public @Nonnull String getChromosome() {
    return m_chromosome;
  }

  public long getPosition() {
    return m_position;
  }

  /**
   * Gets the list of unique identifiers for this position.
   */
  public @Nonnull List<String> getIds() {
    return m_ids;
  }


  /**
   * Gets the reference base(s) for this position.  Each base must be an A, C, G, T, or N.
   */
  public @Nonnull List<String> getRefBases() {
    return m_refBases;
  }

  /**
   * Gets the alternate base(s) for this position.  Each base must be an A, C, G, T, N or * unless it's an
   * angle-bracketed ID string ("<ID>").
   * <p>
   * ID strings should reference a specific ALT metadata (obtainable via {@link VcfMetadata#getAlt(java.lang.String)}).
   */
  public @Nonnull List<String> getAltBases() {
    return m_altBases;
  }

  /**
   * Gets the allele at the given index from a list of containing refBases + altBases.
   *
   * @throws IndexOutOfBoundsException if index is out of range
   */
  public @Nonnull String getAllele(int index) {
    return m_alleles.get(index);
  }


  public @Nullable String getQuality() {
    return m_quality;
  }


  public boolean isPassedAllFilters() {
    return m_filter.equalsIgnoreCase("PASS");
  }

  public @Nullable String getFilter() {
    return m_filter;
  }


  /**
   * Get INFO metadata with the specified ID.
   *
   * @return list of values or null if there is no INFO metadata for the specified id
   */
  public @Nullable List<String> getInfo(@Nonnull String id) {
    if (hasInfo(id)) {
      return m_info.get(id);
    }
    return null;
  }

  /**
   * Returns the value for the reserved property as the type specified by both {@link ReservedInfoProperty#getType()}
   * and {@link ReservedInfoProperty#isList()}.
   * <em>Note that this method does NOT always return a list.</em>
   * For example:
   * <code>
   *   BigDecimal bq = vcfPosition.getInfoConverted(ReservedInfoProperty.BaseQuality);
   * </code>
   * @param <T> The type specified by {@code ReservedInfoProperty.getType()} if {@code ReservedInfoProperty.isList()}
   *           is false;
   *           otherwise {@code List<V>} where V is the type specified by {@code ReservedInfoProperty.getType()}.
   */
  public @Nullable <T> T getInfo(@Nonnull ReservedInfoProperty key) {
    if (!hasInfo(key.getId())) {
      return null;
    }
    List<String> list = m_info.get(key.getId());
    if (list.isEmpty()) {
      return null;
    }
    return PropertyUtils.convertProperty(key, sf_commaJoiner.join(list));
  }

  /**
   * Checks if there is INFO metadata with the specified ID.
   */
  public boolean hasInfo(@Nonnull String id) {
    return m_info != null && m_info.containsKey(id);
  }

  /**
   * Checks if there is INFO metadata with the specified ID.
   */
  public boolean hasInfo(@Nonnull ReservedInfoProperty key) {
    return hasInfo(key.getId());
  }

  public @Nonnull List<String> getFormat() {
    return m_format;
  }
}
