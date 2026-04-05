import { promises as fs } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { defineConfig } from "vite";
import type { Plugin } from "vite";
import react from "@vitejs/plugin-react-swc";
import tsconfigPaths from "vite-tsconfig-paths";
import svgr from "vite-plugin-svgr";
import autoprefixer from "autoprefixer";

const frontendRoot = path.dirname(fileURLToPath(import.meta.url));
const distDir = path.resolve(frontendRoot, "dist");
const monitoringServiceRoot = path.resolve(
  frontendRoot,
  "../Backend/MyApp.MonitoringService",
);
const monitoringSourceMapsDir = path.join(
  monitoringServiceRoot,
  "SourceMaps",
  "frontend-web",
);

const collectMapFiles = async (
  rootDir: string,
  currentDir = rootDir,
): Promise<string[]> => {
  const entries = await fs.readdir(currentDir, { withFileTypes: true });
  const mapFiles: string[] = [];

  for (const entry of entries) {
    const absoluteEntryPath = path.join(currentDir, entry.name);

    if (entry.isDirectory()) {
      const nestedMapFiles = await collectMapFiles(rootDir, absoluteEntryPath);
      mapFiles.push(...nestedMapFiles);
      continue;
    }

    if (!entry.isFile() || !entry.name.endsWith(".map")) {
      continue;
    }

    mapFiles.push(path.relative(rootDir, absoluteEntryPath));
  }

  return mapFiles;
};

const copyMonitoringSourceMapsPlugin = (): Plugin => ({
  name: "copy-monitoring-sourcemaps",
  apply: "build",
  async closeBundle() {
    try {
      await fs.access(monitoringServiceRoot);
    } catch {
      // Frontend Docker builds run outside the monorepo root and can safely skip copying.
      return;
    }

    const mapFiles = await collectMapFiles(distDir);
    await fs.rm(monitoringSourceMapsDir, { recursive: true, force: true });

    for (const relativeMapPath of mapFiles) {
      const sourceMapPath = path.join(distDir, relativeMapPath);
      const destinationMapPath = path.join(monitoringSourceMapsDir, relativeMapPath);

      await fs.mkdir(path.dirname(destinationMapPath), { recursive: true });
      await fs.copyFile(sourceMapPath, destinationMapPath);
    }

    // eslint-disable-next-line no-console
    console.info(
      `[vite] Copied ${mapFiles.length} sourcemap file(s) to ${monitoringSourceMapsDir}`,
    );
  },
});

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    copyMonitoringSourceMapsPlugin(),
    react(),
    tsconfigPaths(),
    svgr({
      include: "**/*.svg?react",
    }),
  ],
  build: {
    sourcemap: true,
  },
  css: {
    modules: {
      localsConvention: "camelCase",
    },
    postcss: {
      plugins: [autoprefixer],
    },
    preprocessorOptions: {
      scss: {
        loadPaths: ["./"],
      },
    },
  },
});
