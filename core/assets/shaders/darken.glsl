#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform mat4 u_projTrans;

float darken(vec3 color, float col){
	col = (col + col + col + color.r + color.g + color.b)/6.0;
    col = col - 0.36;
    if (col < 0) col = 0;
    return col;
}

void main() {
        vec3 color = texture2D(u_texture, v_texCoords).rgb * v_color;
        vec3 old_color = color;
        color.r = darken(old_color, color.r);
        color.g = darken(old_color, color.g);
        color.b = darken(old_color, color.b) + 10/255.0;
        gl_FragColor = vec4(color, texture2D(u_texture, v_texCoords).a);
}