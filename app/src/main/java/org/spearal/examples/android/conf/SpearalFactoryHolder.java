package org.spearal.examples.android.conf;

import org.spearal.DefaultSpearalFactory;
import org.spearal.SpearalFactory;
import org.spearal.impl.alias.PackageTranslatorAliasStrategy;


public class SpearalFactoryHolder {
	
	private static SpearalFactory spearalFactory = null;
	
	public static SpearalFactory getInstance() {
		if (spearalFactory != null)
			return spearalFactory;
		
		spearalFactory = new DefaultSpearalFactory();
		spearalFactory.getContext().configure(new PackageTranslatorAliasStrategy("org.spearal.examples.android", "org.spearal.samples.springangular"));
		return spearalFactory;
	}

    public static <T> T create(Class<T> clazz) {
        return (T)spearalFactory.getContext().newInstance(clazz);
    }

}
