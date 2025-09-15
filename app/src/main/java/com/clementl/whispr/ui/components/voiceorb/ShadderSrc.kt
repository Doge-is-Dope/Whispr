package com.clementl.whispr.ui.components.voiceorb

val shaderSrc = """
    uniform float2 iResolution;
    uniform float  iTime;
    uniform float  uEnergy;
    uniform float4 uColorA;
    uniform float4 uColorB;
    uniform float  uSpeedMin, uSpeedMax;
    uniform float  uGainMin,  uGainMax;
    uniform float  uHaloStrength;
    
    float hash(float2 p){ return fract(sin(dot(p, float2(127.1,311.7))) * 43758.5453123); }
    
    float noise(float2 p){
      float2 i = floor(p), f = fract(p);
      float a = hash(i);
      float b = hash(i + float2(1,0));
      float c = hash(i + float2(0,1));
      float d = hash(i + 1.0);
      float2 u = f*f*(3.0 - 2.0*f);
      return mix(mix(a,b,u.x), mix(c,d,u.x), u.y);
    }
    
    float fbm(float2 p){
      float v = 0.0, a = 0.5;
      for (int i=0; i<5; i++){
        v += a * noise(p);
        p  = p*2.02 + 17.3;
        a *= 0.5;
      }
      return v;
    }
    
    half4 main(float2 fragCoord){
      float2 uv = (fragCoord - 0.5*iResolution) / min(iResolution.x, iResolution.y);
      float  r  = length(uv);
    
      // 半徑脈動（想要「只有有聲音才脈動」→ 把 (1.0+uEnergy) 改成 uEnergy）
      float radius = 0.42 + 0.02 * sin(iTime * 2.2 * (uEnergy));
    
      // 邊緣更銳一些
      float mask = smoothstep(radius, radius - 0.015, r);
    
      // 速度/對比：能量越大越快、越高對比
      float e     = clamp(uEnergy, 0.0, 1.0);
      float speed = mix(uSpeedMin, uSpeedMax, e);
      // 如果要「安靜時完全不動」，就把下一行打開，並把 Kotlin 的 theme.speedMin 設 0：
      // speed *= e;
    
      float gain  = mix(uGainMin, uGainMax, e);
    
      // 雲（原本的雙通道 domain warp）
      float2 q = uv*2.4 + float2(iTime*0.18*speed, -iTime*0.14*speed);
      float2 w = float2(fbm(q + 1.7), fbm(q - 3.1));
      float  m = fbm(q + w*0.9);
    
      // 對比（保留原本的 pow 風格）
      m = clamp(pow(m, gain), 0.0, 1.0);
      m = mix(m*0.65, m, 0.65); // 暗部稍壓
    
      float inner = smoothstep(0.55, 0.0, r);
      float rim   = smoothstep(radius + 0.05, radius - 0.005, r);
    
      // 顏色混合：外色為基、向內色過渡
      float  t   = clamp(m*0.9 + inner*0.25, 0.0, 1.0);
      float4 col = mix(uColorB, uColorA, t);
    
      // 邊緣冷色描邊
      col.rgb += rim * float3(0.08, 0.10, 0.14);
    
      // 圓形遮罩
      col *= mask;
    
      // 外光暈（能量越高越亮）
      float halo = smoothstep(radius + 0.035, radius + 0.11, r);
      col.rgb   += (1.0 - halo) * (uHaloStrength + 0.07*e);
    
      return half4(col);
    }
""".trimIndent()