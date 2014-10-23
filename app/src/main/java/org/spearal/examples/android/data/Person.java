package org.spearal.examples.android.data;

import java.io.Serializable;

/**
 * Simple entity.
 */
public interface Person extends Serializable {

    public Long getId();
    public void setId(Long id);

    public String getName();
    public void setName(String name);

    public String getDescription();
    public void setDescription(String description);

    public String getImageUrl();
    public void setImageUrl(String link);
}
