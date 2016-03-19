package org.jaudio.dsp.aggregators;

import org.dynamicfactory.AbstractFactory;
import org.dynamicfactory.FactoryFactory;
import org.dynamicfactory.descriptors.Properties;

/**
 * Created by Daniel McEnnis on 3/19/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */
public class AggregatorFactory extends AbstractFactory<Aggregator>{
    private static AggregatorFactory ourInstance = new AggregatorFactory();

    public static AggregatorFactory getInstance() {
        return ourInstance;
    }

    private AggregatorFactory() {
        FactoryFactory.newInstance().addType("AggregatorFactory",this);
    }

    @Override
    public AbstractFactory prototype() {
        return getInstance();
    }
}
