import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class COWHashMap<K, V> implements Map<K, V> {

	AtomicReference<HashMap<K, V>> delegate = new AtomicReference<HashMap<K, V>>(new HashMap<K, V>());

	@Override
	public void clear() {
		HashMap<K, V> hm, newhm;
		do {
			hm = delegate.get();
			newhm = new HashMap<K, V>();
		} while (!delegate.compareAndSet(hm, newhm));
	}

	@Override
	public boolean containsKey(Object arg0) {
		return delegate.get().containsKey(arg0);
	}

	@Override
	public boolean containsValue(Object arg0) {
		return delegate.get().containsValue(arg0);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return Collections.unmodifiableSet(delegate.get().entrySet());
	}

	@Override
	public V get(Object arg0) {
		return delegate.get().get(arg0);
	}

	@Override
	public boolean isEmpty() {
		return delegate.get().isEmpty();
	}

	@Override
	public boolean equals(Object obj) {
		return delegate.get().equals(obj);
	}

	@Override
	public int hashCode() {
		return delegate.get().hashCode();
	}

	@Override
	public Set<K> keySet() {
		return Collections.unmodifiableSet(delegate.get().keySet());
	}

	@Override
	public V put(K arg0, V arg1) {
		HashMap<K, V> hm, newhm;
		V toreturn;
		do {
			hm = delegate.get();
			newhm = new HashMap<K, V>(delegate.get());
			toreturn = newhm.put(arg0, arg1);
		} while (!delegate.compareAndSet(hm, newhm));
		return toreturn;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> arg0) {
		HashMap<K, V> hm, newhm;
		do {
			hm = delegate.get();
			newhm = new HashMap<K, V>(delegate.get());
			newhm.putAll(arg0);
		} while (!delegate.compareAndSet(hm, newhm));
	}

	@Override
	public V remove(Object arg0) {
		HashMap<K, V> hm, newhm;
		V toreturn;
		do {
			hm = delegate.get();
			newhm = new HashMap<K, V>(delegate.get());
			toreturn = newhm.remove(arg0);
		} while (!delegate.compareAndSet(hm, newhm));
		return toreturn;
	}

	@Override
	public int size() {
		return delegate.get().size();
	}

	@Override
	public Collection<V> values() {
		return Collections.unmodifiableCollection(delegate.get().values());
	}
}
