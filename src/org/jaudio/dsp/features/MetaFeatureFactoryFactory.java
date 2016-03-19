package org.jaudio.dsp.features;

import org.dynamicfactory.AbstractFactory;
import org.dynamicfactory.descriptors.Properties;

/**
 * Created by Daniel McEnnis on 3/19/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */
public class MetaFeatureFactoryFactory extends AbstractFactory<MetaFeatureFactory> {
    private static MetaFeatureFactoryFactory ourInstance = new MetaFeatureFactoryFactory();

    public static MetaFeatureFactoryFactory getInstance() {
        return ourInstance;
    }

    private MetaFeatureFactoryFactory() {
    }

    @Override
    public MetaFeatureFactory create(Properties props) {
        return null;
    }
}
