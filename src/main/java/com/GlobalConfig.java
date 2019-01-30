package com;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.CRC32;

import com.ccloomi.core.util.Paths;

public class GlobalConfig {
	private static final FileWatch fw=new FileWatch();
	static {
		File pb=Paths.getUserDirFile("public");
		if(pb.exists()) {
			fw.addPath(pb.toPath()).start();
		}
	}
	
	public static String getFileHash(Path f) {
		String hash=fw.getFileHash(f);
		if(hash==null) {
			return Integer.toHexString(f.toString().hashCode());
		}
		return hash;
	}
	
	/**
	 * @date 2018年12月27日 上午9:11:14
	 * @author chenxianjun
	 * @since version
	 */
	private static class FileWatch extends Thread{
		private WatchService watchService;
		private Queue<Path>ps=new ConcurrentLinkedQueue<>();
		private Map<WatchKey, Path>keys=new HashMap<>();
		private Set<Integer>registed=new HashSet<>();
		private Map<Integer, String>fileHash=new HashMap<>();
		public FileWatch() {
			try {
				this.watchService=FileSystems.getDefault().newWatchService();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public FileWatch addPath(Path p) {
			ps.add(p);
			return this;
		}
		public FileWatch register(Path p) {
			try {
				if(p.toFile().isFile()) {
					int hash=p.toString().hashCode();
					if(!fileHash.containsKey(hash)) {
						fileHash.put(hash, getCRC32(p));
					}
					p=p.getParent();
				}
				if(registed.contains(p.toString().hashCode())) {
					return this;
				}
				WatchKey key = p.register(watchService,
						StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_MODIFY,
						StandardWatchEventKinds.ENTRY_DELETE);
				keys.put(key, p);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return this;
		}
		public FileWatch registerDirs(Path...dirs) {
			for(Path dir:dirs) {
				try {
					Files.walkFileTree(dir,new SimpleFileVisitor<Path>() {
						@Override
						public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
							register(dir);
							return FileVisitResult.CONTINUE;
						}
						@Override
						public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
							fileHash.put(file.toString().hashCode(), getCRC32(file));
							return FileVisitResult.CONTINUE;
						}
					});
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
			return this;
		}
		public String getFileHash(Path file) {
			return fileHash.get(file.toString().hashCode());
		}
		@SuppressWarnings("unused")
		public String saveFileHash(Path file) {
			String hash=getCRC32(file);
			fileHash.put(file.toString().hashCode(), hash);
			return hash;
		}
		@SuppressWarnings("unchecked")
		private static <T> WatchEvent<T> cast(WatchEvent<?> event) {
			return (WatchEvent<T>) event;
		}
		private static String getCRC32(Path p) {
			try {
				CRC32 crc32=new CRC32();
				crc32.update(Files.readAllBytes(p));
				return Long.toHexString(crc32.getValue());
			}catch (Exception e) {
				return Integer.toHexString(p.hashCode());
			}
		}
		@Override
		public void run() {
			while (true) {
				if(!ps.isEmpty()) {
					while(!ps.isEmpty()) {
						registerDirs(ps.poll());
					}
				}
				WatchKey key;
				try {
					key=watchService.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				Path path=keys.get(key);
				if(path==null) {
					continue;
				}
				for(WatchEvent<?>e:key.pollEvents()) {
					if(e.kind()==StandardWatchEventKinds.OVERFLOW) {
						continue;
					}
					// 目录内的变化可能是文件或者目录
					WatchEvent<Path> ev = cast(e);
					Path name = ev.context();
					Path child = path.resolve(name);
					if(e.kind()==StandardWatchEventKinds.ENTRY_MODIFY) {
						fileHash.put(child.toString().hashCode(), getCRC32(child));
					}else if(e.kind()==StandardWatchEventKinds.ENTRY_CREATE) {
						fileHash.put(child.toString().hashCode(), getCRC32(child));
						try {
							if(Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
								registerDirs(child);
							}
						}catch (Exception ee) {
						}
					}else if(e.kind()==StandardWatchEventKinds.ENTRY_DELETE) {
						fileHash.remove(child.toString().hashCode());
					}else {
						continue;
					}
				}
				if(!key.reset()) {
					Path p=keys.remove(key);
					registed.remove(p.toString().hashCode());
				}
			}
		}
	}
}
