package fr.maxlego08.stats.placeholder;
@FunctionalInterface
public interface ReturnBiConsumer<T, G, C> {

	C accept(T t, G g);
	
}