package org.pharmgkb.parser.vcf.model;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * A metadata with no real ID. So, we pretend that the raw text of the metadata (without the ##) is the ID.
 */
public final class RawMetadata extends IdMetadata {

  public RawMetadata(@Nonnull String id) {
    super();
    putPropertyRaw(ID, id);
  }

  @Nonnull
  public String asVcfString() {
    return "##" + getId();
  }

  @Nonnull
  @Override
  public String asVcfString(@Nonnull String metadataTypeName) {
    return "##" + getId();
  }

}
