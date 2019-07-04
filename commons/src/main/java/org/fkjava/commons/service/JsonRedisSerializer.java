package org.fkjava.commons.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

import org.fkjava.commons.domain.InMessage;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonRedisSerializer extends Jackson2JsonRedisSerializer<Object> {

	private ObjectMapper objectMapper = new ObjectMapper();

	public JsonRedisSerializer() {
		super(Object.class);
	}

	// 序列化对象的时候被调用的方法，负责把InMessage转换为byte[]
	@Override
	public byte[] serialize(Object t) throws SerializationException {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();// 把数据输出到一个字节数组
		DataOutputStream out = new DataOutputStream(baos);// 把输出流封装成数据输出流
		try {
			String className = t.getClass().getName();// 获取类名
			byte[] classNameBytes = className.getBytes("UTF-8");

			out.writeInt(classNameBytes.length);// 先把类名的长度写出去
			out.write(classNameBytes);// 把类名转换得到的字节数组写出

			// 使用原本父类的方法，负责把对象转换为字节数组
			byte[] data = super.serialize(t);
			out.write(data);

			// 得到结果数组
			byte[] result = baos.toByteArray();
			return result;
		} catch (Exception e) {
			throw new SerializationException("序列化对象出现问题：" + e.getLocalizedMessage(), e);
		}
//		return super.serialize(t);
	}

	// 在反序列化的时候被调用的方法，负责把字节数组转换为InMessage
	@Override
	public InMessage deserialize(byte[] bytes) throws SerializationException {

		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream in = new DataInputStream(bais);

		// 在写的时候，先把类名的长度传入，此时要先得到类名的长度，再根据类名的长度来读取类名。
		try {
			int length = in.readInt();
			byte[] classNameBytes = new byte[length];
			// 把字节数组填满才返回
			in.readFully(classNameBytes);
			// 把读取到的字节数组，转换为类名
			String className = new String(classNameBytes, "UTF-8");
			// 通过类名，加载类对象
			@SuppressWarnings("unchecked")
			Class<? extends InMessage> cla = (Class<? extends InMessage>) Class.forName(className);

			// length + 4 : 表示类名的长度和int的长度，一个int占4个字节
			return this.objectMapper.readValue(Arrays.copyOfRange(bytes, length + 4, bytes.length), cla);
		} catch (Exception e) {
			throw new SerializationException("反序列化对象出现问题：" + e.getLocalizedMessage(), e);
		}

//		return super.deserialize(bytes);
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
}

