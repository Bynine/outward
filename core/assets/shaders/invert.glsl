#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

vec3 twobit(vec3 color){
    if (color.r > 0/255.0){
        color.r = 0.0/255.0;
    	color.g = 12.0/255.0;
    	color.b = 28.0/255.0;
    }
    else {
    	color.r = 89.0/255.0;
    	color.g = 74.0/255.0;
    	color.b = 78.0/255.0;
    }
    return color;
}

void main() {
        vec3 color = twobit(texture2D(u_texture, v_texCoords).rgb * v_color);
        gl_FragColor = vec4(color, texture2D(u_texture, v_texCoords).a);
}