import { NextResponse } from 'next/server';
import { z } from 'zod';

const loginSchema = z.object({
  username: z.string().min(1, 'Username is required.'),
  password: z.string().min(1, 'Password is required.'),
});

export async function POST(req: Request) {
  try {
    const body = await req.json();
    const { username, password } = loginSchema.parse(body);

    console.log('Frontend login API called, forwarding to Spring Boot backend...');

    // Forward the login request to the Spring Boot backend
    const backendResponse = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ username, password }),
    });

    const backendData = await backendResponse.json();
    console.log('Backend login response:', backendData);

    if (!backendResponse.ok) {
      return NextResponse.json(
        { success: false, message: backendData.message || 'Login failed' },
        { status: backendResponse.status }
      );
    }

    // Return the backend response which includes JWT tokens
    return NextResponse.json({
      success: true,
      data: backendData,
      message: 'Login successful'
    });

  } catch (error) {
    if (error instanceof z.ZodError) {
      return NextResponse.json({ success: false, errors: error.errors }, { status: 400 });
    }
    console.error("[LOGIN_POST]", error);
    return NextResponse.json({ success: false, message: 'Internal Server Error' }, { status: 500 });
  }
}