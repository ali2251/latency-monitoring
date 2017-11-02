package org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import org.opendaylight.yangtools.yang.binding.AugmentationHolder;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.concepts.Builder;
import java.util.Objects;
import java.util.Collections;
import java.util.Map;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput} instances.
 *
 * @see org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput
 *
 */
public class GetStatsOutputBuilder implements Builder <org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput> {

    private java.lang.String _stats;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>> augmentation = Collections.emptyMap();

    public GetStatsOutputBuilder() {
    }

    public GetStatsOutputBuilder(GetStatsOutput base) {
        this._stats = base.getStats();
        if (base instanceof GetStatsOutputImpl) {
            GetStatsOutputImpl impl = (GetStatsOutputImpl) base;
            if (!impl.augmentation.isEmpty()) {
                this.augmentation = new HashMap<>(impl.augmentation);
            }
        } else if (base instanceof AugmentationHolder) {
            @SuppressWarnings("unchecked")
            AugmentationHolder<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput> casted =(AugmentationHolder<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>) base;
            if (!casted.augmentations().isEmpty()) {
                this.augmentation = new HashMap<>(casted.augmentations());
            }
        }
    }


    public java.lang.String getStats() {
        return _stats;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

     
    public GetStatsOutputBuilder setStats(final java.lang.String value) {
        this._stats = value;
        return this;
    }
    
    public GetStatsOutputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput> augmentation) {
        if (augmentation == null) {
            return removeAugmentation(augmentationType);
        }
    
        if (!(this.augmentation instanceof HashMap)) {
            this.augmentation = new HashMap<>();
        }
    
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }
    
    public GetStatsOutputBuilder removeAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>> augmentationType) {
        if (this.augmentation instanceof HashMap) {
            this.augmentation.remove(augmentationType);
        }
        return this;
    }

    public GetStatsOutput build() {
        return new GetStatsOutputImpl(this);
    }

    private static final class GetStatsOutputImpl implements GetStatsOutput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput.class;
        }

        private final java.lang.String _stats;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>> augmentation = Collections.emptyMap();

        private GetStatsOutputImpl(GetStatsOutputBuilder base) {
            this._stats = base.getStats();
            switch (base.augmentation.size()) {
            case 0:
                this.augmentation = Collections.emptyMap();
                break;
            case 1:
                final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>> e = base.augmentation.entrySet().iterator().next();
                this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>>singletonMap(e.getKey(), e.getValue());
                break;
            default :
                this.augmentation = new HashMap<>(base.augmentation);
            }
        }

        @Override
        public java.lang.String getStats() {
            return _stats;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        private int hash = 0;
        private volatile boolean hashValid = false;
        
        @Override
        public int hashCode() {
            if (hashValid) {
                return hash;
            }
        
            final int prime = 31;
            int result = 1;
            result = prime * result + Objects.hashCode(_stats);
            result = prime * result + Objects.hashCode(augmentation);
        
            hash = result;
            hashValid = true;
            return result;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof DataObject)) {
                return false;
            }
            if (!org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput other = (org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput)obj;
            if (!Objects.equals(_stats, other.getStats())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                GetStatsOutputImpl otherImpl = (GetStatsOutputImpl) obj;
                if (!Objects.equals(augmentation, otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.GetStatsOutput>> e : augmentation.entrySet()) {
                    if (!e.getValue().equals(other.getAugmentation(e.getKey()))) {
                        return false;
                    }
                }
                // .. and give the other one the chance to do the same
                if (!obj.equals(this)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public java.lang.String toString() {
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("GetStatsOutput [");
            boolean first = true;
        
            if (_stats != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_stats=");
                builder.append(_stats);
             }
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append("augmentation=");
            builder.append(augmentation.values());
            return builder.append(']').toString();
        }
    }

}
