package moe.nea.notfimament.init;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import org.objectweb.asm.Type;

import java.util.List;

public class Intermediary {
	private static final MappingResolver RESOLVER = FabricLoader.getInstance().getMappingResolver();

	static InterMethod intermediaryMethod(Object object, InterClass returnType, InterClass... args) {
		throw new AssertionError("Cannot be called at runtime");
	}

	static <T> InterClass intermediaryClass() {
		throw new AssertionError("Cannot be called at runtime");
	}

	public static InterClass ofIntermediaryClass(String interClass) {
		return new InterClass(Type.getObjectType(interClass.replace('.', '/')));
	}

	public static InterClass ofClass(Class<?> unmappedClass) {
		return new InterClass(Type.getType(unmappedClass));
	}

	public static InterMethod ofMethod(String intermediary, String ownerType, InterClass returnType, InterClass... argTypes) {
		return new InterMethod(intermediary, ofIntermediaryClass(ownerType), returnType, List.of(argTypes));
	}

	public record InterClass(
		Type intermediary
	) {
		public Type mapped() {
			if (intermediary().getSort() != Type.OBJECT)
				return intermediary();
			return Type.getObjectType(RESOLVER.mapClassName("intermediary", intermediary().getClassName())
				.replace('.', '/'));
		}
	}

	public record InterMethod(
		String intermediary,
		InterClass ownerType,
		InterClass returnType,
		List<InterClass> argumentTypes
	) {
		public Type intermediaryDesc() {
			return Type.getMethodType(
				returnType.intermediary(),
				argumentTypes().stream().map(InterClass::intermediary).toArray(Type[]::new)
			);
		}

		public Type mappedDesc() {
			return Type.getMethodType(
				returnType.mapped(),
				argumentTypes().stream().map(InterClass::mapped).toArray(Type[]::new)
			);
		}

		public String mapped() {
			return RESOLVER.mapMethodName(
				"intermediary",
				ownerType.intermediary().getClassName(),
				intermediary(),
				intermediaryDesc().getDescriptor()
			);
		}
	}
}
