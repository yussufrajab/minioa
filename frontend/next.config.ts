import type {NextConfig} from 'next';

const nextConfig: NextConfig = {
  /* config options here */
  typescript: {
    ignoreBuildErrors: true,
  },
  eslint: {
    ignoreDuringBuilds: true,
  },
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'placehold.co',
        port: '',
        pathname: '/**',
      },
    ],
  },
  // Three-tier architecture configuration
  async rewrites() {
    return [
      // Rewrite API calls to backend during development
      // Exclude Next.js API routes we want to handle locally
      {
        source: '/api/:path((?!auth/employee-login|test-api).*)',
        destination: `${process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080'}/api/:path*`,
      },
    ];
  },
  // Environment variables for build time
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api',
    NEXT_PUBLIC_BACKEND_URL: process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080',
  },
};

export default nextConfig;
