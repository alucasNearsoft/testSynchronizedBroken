import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Broken - invokes alien method from synchronized block!
public class ObservableSet<E> extends ForwardingSet<E> {
	public ObservableSet(Set<E> set) { super(set); }
	private final List<SetObserver<E>> observers =
			new ArrayList<SetObserver<E>>();
	public void addObserver(SetObserver<E> observer) {
		synchronized(observers) {
			observers.add(observer);
		}
	}
	public boolean removeObserver(SetObserver<E> observer) {
		synchronized(observers) {
			return observers.remove(observer);
		}
	}
	private void notifyElementAdded(E element) {
		synchronized(observers) {
			for (SetObserver<E> observer : observers)
				observer.added(this, element);
		}
	}
	@Override public boolean add(E element) {
		boolean added = super.add(element);
		if (added)
			notifyElementAdded(element);
		return added;
	}
	@Override public boolean addAll(Collection<? extends E> c) {
		boolean result = false;
		for (E element : c)
			result |= add(element); // calls notifyElementAdded
		return result;
	}
	
	public static void main(String[] args) {
		ObservableSet<Integer> set =
				new ObservableSet<Integer>(new HashSet<Integer>());
		set.addObserver(new SetObserver<Integer>() {
			public void added(ObservableSet<Integer> s, Integer e) {
				System.out.println(e);
			}
		});
		for (int i = 0; i < 100; i++)
			set.add(i);
	}
}